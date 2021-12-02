package com.example.kotlinomnicure.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.adapter.UrgentMessageAdapter
import com.example.kotlinomnicure.databinding.ActivityViewUrgentMessagesLayoutBinding
import com.example.kotlinomnicure.model.ConsultMessage
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import java.lang.Exception
import java.util.*

class ViewUrgentMessagesActivity : BaseActivity() {
    // Variables
    private var context: Context? = null
    private val TAG = ViewUrgentMessagesActivity::class.java.simpleName
    private var binding: ActivityViewUrgentMessagesLayoutBinding? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var senderId: String? = null
    private val imageURL: String? = null
    private val mUsername: String? = null
    private var role: String? = null
    private val mProviderUid: String? = null
    private var MESSAGES_CHILD: String? = null
    private var inviteTime: Long? = null
    private var dischargeTime: Long? = null
    var status: String? = null
    private val LOADING_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/omnicure.appspot.com/o/spin-32.gif?alt=media&token=f5a877ef-bf5b-4d54-85da-a30a5d8ce98d" //"https://www.google.com/images/spin-32.gif";
    private val query: Query? = null
    private val MEMBERS_CHILD: String? = null
    private var encKey: String? = null
    private val itemCount = -1
    private val currentUser: Provider? = null
    private val mLinearLayoutManager: LinearLayoutManager? = null
    private val mFirebaseDatabaseReference: DatabaseReference? = null
    var messageList: ArrayList<ConsultMessage?> = ArrayList<ConsultMessage?>()
    var adapter: UrgentMessageAdapter? = null
    private var messageDB: DatabaseReference? = null

    var dbListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            handleVisibility()
            if (isFirstTime) {
                try {
                    isFirstTime = false
                    if (dataSnapshot.value != null && dataSnapshot.value is Map<*, *>) {
                        val membersMap: Map<*, *>? = dataSnapshot.value as Map<*, *>?
                        messageList.clear()
                        for (s in membersMap!!.keys) {
//                            Log.d(TAG, "snap values " + s + " : " + membersMap.get(s));
                            val gson = Gson()
                            val `val` = gson.toJson(membersMap[s])
                            val consultMessage = gson.fromJson(`val`, ConsultMessage::class.java)
                            if (consultMessage != null) {
                                consultMessage.setId(s as String?)
                                if (isIncludeChat(consultMessage)) {
                                    messageList.add(consultMessage)
                                    messageList.sortWith(Comparator { cm1, cm2 ->
                                        if (cm1 == null || cm2 == null || cm1.getTime()?.equals(0) == true || cm2.getTime()?.equals(0) == true
                                        ) {
                                            Int.MIN_VALUE
                                        } else cm2.getTime()!!.compareTo(cm1.getTime()!!)
                                    })
                                    //                                    Log.d(TAG, "parseSnapshot getKey" + new Gson().toJson(consultMessage));
                                    adapter!!.notifyDataSetChanged()
                                    if (messageList.size > 0) {
                                        Handler().postDelayed({
                                            binding!!.recyclerView.smoothScrollToPosition(0)
                                        }, 1000)
                                    }
                                }
                            }
                        }
                        handleVisibility()
                    }
                } catch (e: Exception) {
//                    Log.d("Exception", e.toString());
                    handleVisibility()
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
//            Log.d(TAG, "onCancelled: " + databaseError);
        }
    }
    private val strUID: String? = null
    private var isFirstTime = true
    private var id: String? = null


    fun handleVisibility() {
        if (messageList.size <= 0) {
            binding?.noRecordsLayout?.setVisibility(View.VISIBLE)
            binding?.recyclerView?.setVisibility(View.GONE)
        } else {
            binding?.noRecordsLayout?.setVisibility(View.GONE)
            binding?.recyclerView?.setVisibility(View.VISIBLE)
        }
    }

    fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        binding?.recyclerView?.setLayoutManager(linearLayoutManager)

        val encKey: String? = PrefUtility().getAESAPIKey(this)
        adapter = encKey?.let { senderId?.let { it1 ->
            id?.let { it2 ->
                UrgentMessageAdapter(this, messageList, it,
                    it1, it2)
            }
        } }
        binding?.recyclerView?.setAdapter(adapter)
        adapter?.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Databinding and view model intialization
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_view_urgent_messages_layout)
        //Firebase intialization
        mFirebaseAuth = FirebaseAuth.getInstance()
        // Getting the current user from firebase
        mFirebaseUser = mFirebaseAuth!!.currentUser
        context = this

//        MESSAGES_CHILD = getIntent().getStringExtra("path");
        MESSAGES_CHILD = intent.getStringExtra("path").toString() + "/messages"
        inviteTime = intent.extras!!.getLong("inviteTime")
        dischargeTime = intent.extras!!.getLong("dischargeTime")
        status = intent.extras!!.getString("status")
        //  strUID = getIntent().getExtras().getString("id");
        id = java.lang.String.valueOf(intent.getLongExtra("id", 0))

//        Log.e(TAG, "onCreate:STRUID " + id);
        setView()
        setAdapter()
        messageDB = FirebaseDatabase.getInstance().reference.child(MESSAGES_CHILD!!)
        messageDB!!.addValueEventListener(dbListener)

        // Toolbar initialization
        initToolbar()
        //Setting up the view for the activity


        // Setting up the firebase adapter
        //firebaseAdapterInitialization();

        // Move to chat is false while initiated
        PrefUtility().saveStringInPref(this,
       Constants.SharedPrefConstants.MOVE_TO_CHAT_ID, "")
    }

    /**
     * Firebase intialization setup
     */
    private fun firebaseAdapterInitialization() {

        // Firebase new data observer method
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                //Log.d(TAG, "onItemRangeInserted: "+ positionStart +", "+ itemCount);
                val friendlyMessageCount: Int = adapter!!.getItemCount()
                val lastVisiblePosition =
                    mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                    positionStart >= friendlyMessageCount - 1 &&
                    lastVisiblePosition == positionStart - 1
                ) {
                    binding?.recyclerView?.scrollToPosition(positionStart)
                }
                //mMessageRecyclerView.scrollToPosition(friendlyMessageCount-1);
            }
        })
    }

    /**
     * @param consultMessage
     * @param binding
     * @param position
     * @param ref
     */

    /**
     * @param consultMessage
     * @param binding
     * @param position
     * @param ref
     */
    /**
     * Setting up the view for the activity
     */
    private fun setView() {
        // Retrieve the sender Id from shared preference
        senderId =
            java.lang.String.valueOf(PrefUtility().getProviderId(this))
        // Retrieve the user role from shared preference
        role = PrefUtility().getRole(this)
        // Retrieve user name from shared preference

        // Encryption/Decryption process
        EncUtil().generateKey(this)
        // Key to decrypt

        encKey = PrefUtility().getAESAPIKey(this)
        // Back button click listener
        binding?.imgBack?.setOnClickListener { v -> finish() }
    }


    /**
     * Toolbar initialization
     */
    private fun initToolbar() {
        //Action bar support
        setSupportActionBar(binding?.toolbar)
        // Adding the back button
//        addBackButton();
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
    }

    /**
     * Enum message type
     */
    private enum class MessageType {
        system, text, Discharged, Exit, image, file, video
    }

    /**
     * File icons
     *
     * @param consultMessage
     * @return
     */
    private fun getFileIcon(consultMessage: ConsultMessage?): Int {
        if (consultMessage == null || TextUtils.isEmpty(consultMessage.getFilename())) {
            return R.drawable.ic_generic
        }
        var fileName: String? = consultMessage.getFilename()?.lowercase(Locale.getDefault())
        if (fileName?.contains(".txt") == true) {
            return R.drawable.ic_txt
        } else if (fileName?.contains(".doc") == true || fileName?.contains(".docx") == true) {
            return R.drawable.ic_doc
        } else if (fileName?.contains(".pdf") == true) {
            return R.drawable.ic_pdf
        } else if (fileName?.contains(".xls") == true || fileName?.contains(".xlsx") == true) {
            return R.drawable.ic_xls
        } else return if (fileName?.contains(".ppt") == true || fileName?.contains(".pptx") == true) {
            R.drawable.ic_ppt
        } else {
            R.drawable.ic_generic
        }
    }

    /**
     * Include the urgent messages with invite/discharge/complete filter
     * @param consultMessage
     * @return
     */
    private fun isIncludeChat(consultMessage: ConsultMessage?): Boolean {
        if (dischargeTime == 0L || !status.equals(Constants.PatientStatus.Completed.toString(),
                ignoreCase = true)
        ) {
            dischargeTime = Date().time
        }


//        }
        val isTypeIncluded = ((consultMessage?.getType() != null &&
                consultMessage.getType().equals(MessageType.system.toString(),ignoreCase = true)) ||
                consultMessage?.getType().equals(MessageType.text.toString(),ignoreCase = true) ||
                consultMessage?.getType().equals(MessageType.image.toString(),ignoreCase = true) ||
                consultMessage?.getType().equals(MessageType.video.toString(),ignoreCase = true) ||
                consultMessage?.getType().equals(MessageType.file.toString(),ignoreCase = true))


        return isTypeIncluded && consultMessage?.isUrgent() == true
    }

    protected override fun addBackButton() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            binding?.toolbar?.setNavigationIcon(R.drawable.ic_back)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        messageDB!!.removeEventListener(dbListener)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Stop listening the firebase path data
    }

}