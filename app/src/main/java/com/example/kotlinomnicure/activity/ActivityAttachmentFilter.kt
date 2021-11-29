package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.adapter.AttachmentGridAdapter
import com.example.dailytasksamplepoc.databinding.ActivityAttachmentBinding
import com.example.dailytasksamplepoc.kotlinomnicure.model.ConsultMessage
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.google.firebase.database.*

import com.google.gson.Gson

import java.lang.Exception
import java.util.*

class ActivityAttachmentFilter : BaseActivity(), AttachmentGridAdapter.AttachmentListener{
    //Declare the variables
    private val TAG = ActivityAttachmentFilter::class.java.simpleName
    protected var binding: ActivityAttachmentBinding? = null
    var messageList = ArrayList<ConsultMessage>()
    var adapter: AttachmentGridAdapter? = null
    var numberOfColumns = 4
    var inviteTime: Long? = null
    var dischargeTime: Long? = null
    private var messageDB: DatabaseReference? = null

    private enum class MessageType {
        image, file, video
    }

    //ValueEventListener
    var dbListener: ValueEventListener = object : ValueEventListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.d(TAG, "onDataChange Attachment: $dataSnapshot")
            try {
                if (dataSnapshot.value is Map<*, *>) {
                    val membersMap: Map<*, *>? = dataSnapshot.value as Map<*, *>?
                    messageList.clear()
                    for (s in membersMap!!.keys) {
                        Log.d(TAG,  " : " + membersMap[s])
                        val gson = Gson()
                        val `val` = gson.toJson(membersMap[s])
                        val consultMessage = gson.fromJson(`val`, ConsultMessage::class.java)
                        if (consultMessage != null) {
                            consultMessage.id = dataSnapshot.key
                            if (isIncludeChat(consultMessage)) {
                                messageList.add(consultMessage)
                                messageList.sortWith(Comparator sort@{ cm1: ConsultMessage?, cm2: ConsultMessage? ->
                                    if (cm1 == null || cm2 == null || cm1.time == 0L || cm2.time == 0L) {
                                        return@sort Int.MIN_VALUE
                                    }
                                    cm2.time.compareTo(cm1.time)
                                })
                                Log.d(
                                    TAG,
                                    "parseSnapshot getKey" + consultMessage.type
                                )
                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                    handleVisibility()
                }
            } catch (e: Exception) {
                Log.e(TAG, "exception", e.cause)
                handleVisibility()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, "onCancelled: $databaseError"
            )
        }
    }



    //on create method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.attachment_filter)
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics)
        numberOfColumns = (resources.displayMetrics.widthPixels / px).toInt()
        Log.d("no of columns ", numberOfColumns.toString())
        val extras = intent.extras

       //Initialise the variables
        inviteTime = extras!!.getLong("inviteTime")
        dischargeTime = extras.getLong("dischargeTime")
        val messageChild = extras.getString("messageChild")
        messageDB = FirebaseDatabase.getInstance().reference.child(messageChild!!)
        messageDB!!.addValueEventListener(dbListener)
        initViews()
        setAdapter()
    }

    fun setView() {

    }

    //set the adapter
    private fun setAdapter() {
        binding!!.recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        val encKey = EncUtil().decrypt(this, PrefUtility().getAESKey(this))

        adapter = AttachmentGridAdapter(this, messageList, encKey)
        adapter?.setListener(this)
        binding!!.recyclerView.adapter = adapter
    }


   //Init views
    private fun initViews() {
        binding!!.imgBack.setOnClickListener { view ->
            finish()
        }
    }

    //filter the consult message
        fun isIncludeChat(consultMessage: ConsultMessage): Boolean {
            if (dischargeTime == 0L) {
                dischargeTime = Date().time
            }

        //consult message may be video,image,file
            val isFile = consultMessage.type.equals(
                MessageType.image.toString(),
                ignoreCase = true
            ) ||
                    consultMessage.type.equals(
                        MessageType.video.toString(),
                        ignoreCase = true
                    ) || consultMessage.type.equals(
                MessageType.file.toString(), ignoreCase = true
            )
            return if (isFile) {
                inviteTime != null && consultMessage.time / 1000 >= inviteTime!! / 1000 && consultMessage.time / 1000 <= dischargeTime!! / 1000
            } else {
                false
            }


        }


   //handle the visibility layout
    fun handleVisibility()
    {
        if (messageList.isEmpty()) {
            binding!!.noRecordsLayout.visibility = View.VISIBLE
            binding!!.recyclerView.visibility = View.GONE
        } else {
            binding!!.noRecordsLayout.visibility = View.GONE
            binding!!.recyclerView.visibility = View.VISIBLE
        }
    }

    //on clicked the consult message type
    override fun onItemClicked(url: String?, type: String?) {
        if (MessageType.image.toString().equals(type, ignoreCase = true)) {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, url)
            startActivity(intent)
        }
        if (MessageType.video.toString().equals(type, ignoreCase = true)) {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, url)
            startActivity(intent)
        }

        if (MessageType.file.toString().equals(type, ignoreCase = true)) {
            val intent = Intent(this, PDFViewerActivity::class.java)
            intent.putExtra(Constants.IntentKeyConstants.PDF_URL, url)
            startActivity(intent)
        }
    }


}