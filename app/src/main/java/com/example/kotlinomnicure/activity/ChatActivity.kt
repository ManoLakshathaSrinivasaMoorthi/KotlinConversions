package com.example.kotlinomnicure.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

import com.example.kotlinomnicure.adapter.PatientChatHistoryAdapter
import com.example.kotlinomnicure.customview.ChatAttachmentPopup

import com.example.kotlinomnicure.utils.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser

import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.BuildConfig

import com.google.firebase.storage.*
import com.google.gson.Gson
import omnicurekotlin.example.com.patientsEndpoints.model.PatientHistory
import omnicurekotlin.example.com.providerEndpoints.model.Members

import com.example.kotlinomnicure.helper.PBMessageHelper
import com.example.kotlinomnicure.interfaces.OnPatientHistoryItemListener

import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity() : DrawerActivity(), ChatEditTextListener,
    OnPatientHistoryItemListener, BaseActivity(), OnPatientHistoryItemListener {

    //variables
    private val membersList: MutableList<Members>? = ArrayList<Members>()
    private val moveToChat = false
    private val moveToChatHashMap: LinkedHashMap<Int, ConsultMessage> =
        LinkedHashMap<Int, ConsultMessage>()
    var containerParent: FrameLayout? = null
    var ll_chat_popup: LinearLayout? = null
    var uploadInProgress = false
    var isImageUploadFailed = false
    var teamButtonEnabled = false
    var teamButtonView: View? = null
    var defaultImageBitmap: Bitmap? = null
    var uploadType: String? = null
    var retryCount = 0
    var buttonPosition = -1
    var encKey: String? = null
    var senderId: String? = null

    // Mark as urgent popup window
    var popupWindow: PopupWindow? = null
    var lastMessageKey: String? = null
    var moveToChatId: String? = null
    var sortResetFlag = true
    var messageId = 0L
    private var MESSAGES_CHILD: String? = null
    private var MEMBERS_CHILD: String? = null
    private var patientstatus: Constants.PatientStatus? = null
    private var mUsername: String? = null


    private var currentUser: Provider? = null
        private get() {
            if (field == null) {
                field = PrefUtility().getProviderObject(this)
            }
            return field
        }
    private val mPhotoUrl: String? = null
    private var mMessageRecyclerView: RecyclerView? = null
    private var mLinearLayoutManager: LinearLayoutManager? = null

    // Firebase instance variables
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mFirebaseUser: FirebaseUser? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private var mFirebaseAdapter: FirebaseRecyclerAdapter<ConsultMessage?, MessageViewHolder?>? = null
    private var mConsultProvider: ConsultProvider? = null
    private var mConsultProviderKey: String? = null
    private var strConsultTeamName: String? = null
    private var strChartRemote: String? = null
    private var strUid: Long? = null
    private var role: String? = null
    private var mProviderUid: String? = null
    var mrReadListener: ValueEventListener? = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            // message list
            for (ds2: DataSnapshot in dataSnapshot.children) {
                if (ds2.child("ChatMessageStatusList").child(mProviderUid!!).exists()) {
                    val valStatus = ds2.child("ChatMessageStatusList").child(mProviderUid!!)
                        .child("status").getValue(String::class.java)
                    val key = ds2.key
                    if ("Received".equals(valStatus, ignoreCase = true) ||
                        "sent".equals(valStatus, ignoreCase = true)
                    ) {
                        assert(key != null)
                        mFirebaseDatabaseReference!!.child("consults").child(strUid.toString())
                            .child("messages").child(key!!).child("ChatMessageStatusList").child(
                                mProviderUid!!
                            )
                            .child("status").setValue("Read")
                        mFirebaseDatabaseReference!!.child("consults").child(strUid.toString())
                            .child("messages").child(key).child("ChatMessageStatusList")
                            .child(mProviderUid!!).child("time")
                            .setValue(System.currentTimeMillis())
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }
    private var sendApiSuccessful = false
    private var membersMap: MutableMap<String, String>? = HashMap()
    private var chatActBinding: ActivityChatBinding? = null
    private var expand = false
    private var chatViewModel: ChatActivityViewModel? = null
    private var errorTeams: String? = null
    private var customDialog: CustomDialog? = null
    private var downloadPB: DownloadProgressDialog? = null
    private var defaultImageDrawable: Drawable? = null
    private var popup: ChatAttachmentPopup? = null
    private var mFileUploadTask: UploadTask? = null
    private var mMessageKey: String? = null
    private var mFileDownloadTask: TaskConsultMessage? = null
    private val itemCount = -1
    private val selectedItem = 0
    private val menu: Menu? = null
    private var patientHistoryListBackup: List<PatientHistory>? = ArrayList<PatientHistory>()
    private var patientHistoryList: MutableList<PatientHistory?>? = null
    private var historyAdapter: PatientChatHistoryAdapter? = null
    private var patientHistory: PatientHistory? = null
    private var isInvited = false
    private var isCompleted = false
    private var isShowingSelectedChatHisotry = false
    private var mUnreadResetDB: DatabaseReference? = null
    private var mreadDB: DatabaseReference? = null
    private var mViewDetailsListenerDataRef: DatabaseReference? = null
    private var audioCall: MenuItem? = null
    private var videoIcon: MenuItem? = null
    private var directoryMenu: MenuItem? = null
    private var markCompleteMenu: MenuItem? = null
    private var transferMenu: MenuItem? = null
    private var handOffPatient: MenuItem? = null
    private var transferPatient: MenuItem? = null
    private var patientDetails: MenuItem? = null
    private var completeConsultation: MenuItem? = null
    var mUnreadResetListner: ValueEventListener? = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val consultProviderA: ConsultProvider? =
                dataSnapshot.getValue(ConsultProvider::class.java)
            if (consultProviderA != null) {
                if (consultProviderA.getUnread() > 0) {
                    consultProviderA.setUnread(0)
                    mFirebaseDatabaseReference!!.child("providers").child(mProviderUid!!)
                        .child("active").child(
                            mConsultProviderKey!!
                        ).child("unread").setValue(0)
                }
                if (consultProviderA.getStatus() == null) {
                    return
                }
                if (isShowingSelectedChatHisotry && mConsultProvider != null && mConsultProvider!!.getStatus()
                        .equals(consultProviderA.getStatus())
                ) {
                    return
                }
                if (consultProviderA.getStatus() === Constants.PatientStatus.Completed && mConsultProvider != null && mConsultProvider!!.getStatus() !== Constants.PatientStatus.Completed) {
                    showPatientRemovedDialog(consultProviderA.getStatus().toString())
                }

                mConsultProvider?.setStatus(consultProviderA.getStatus())
                if (consultProviderA.getStatus() === Constants.PatientStatus.Completed) {
                    isCompleted = true
                    isInvited = false
                    handleChatEditView(isCompleted, isInvited)
                } else if (consultProviderA.getStatus() === Constants.PatientStatus.Discharged) {
                    isCompleted = true
                    isInvited = false
                    handleChatEditView(isCompleted, isInvited)
                } else if (consultProviderA.getStatus() === Constants.PatientStatus.Invited) {
                    isCompleted = false
                    isInvited = true
                    handleChatEditView(isCompleted, isInvited)
                } else if (consultProviderA.getStatus() === Constants.PatientStatus.Handoff) {
                    isCompleted = false
                    isInvited = true
                    handleChatEditView(isCompleted, isInvited)
                } else if (consultProviderA.getStatus() === Constants.PatientStatus.Pending) {
                    isCompleted = false
                    isInvited = false
                    handleChatEditView(isCompleted, isInvited)
                } else {
                    isCompleted = false
                    isInvited = false
                    handleChatEditView(isCompleted, isInvited)
                }
            } else {
                getPatientDetails()
            }
            handleMenuItems()
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }
    private var intentDone = false
    private var mConsultMessage: ConsultMessage? = null

    // View details value event listener
    var mViewDetailsValueEventListener: ValueEventListener? = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            if (intentDone) {
                return
            }
            // Get Post object and use the values to update the UI
            if (snapshot.exists()) {
                dismissProgressBar()
                if (popupWindow!!.isShowing) {
                    popupWindow!!.dismiss()
                }
                var visibleStatus = false
                if (mConsultProvider.getStatus().equals(Constants.PatientStatus.Invited) ||
                    mConsultProvider.getStatus().equals(Constants.PatientStatus.Pending)
                ) {
                    visibleStatus = true
                }
                intentDone = true
                val intentView = Intent(this, ViewDetailsActivity::class.java)
                intentView.putExtra(
                    Constants.IntentKeyConstants.FIRST_NAME,
                    mConsultProvider?.name
                )
                intentView.putExtra(Constants.IntentKeyConstants.MESSAGE_OBJECT, mConsultMessage)
                intentView.putExtra(Constants.IntentKeyConstants.STR_UID, strUid.toString())
                intentView.putExtra(Constants.IntentKeyConstants.PATIENT_STATUS, visibleStatus)
                intentView.putExtra(Constants.IntentKeyConstants.MESSAGE_CHILD, MESSAGES_CHILD)
                intentView.putExtra(
                    Constants.IntentKeyConstants.CONSULT_PROVIDER,
                    mConsultProvider as Serializable?
                )
                startActivity(intentView)
            } else if (!snapshot.exists()) {


                if (popupWindow!!.isShowing) {
                    popupWindow!!.dismiss()
                }
                var visibleStatus = false
                if (mConsultProvider.getStatus().equals(Constants.PatientStatus.Invited) ||
                    mConsultProvider.getStatus().equals(Constants.PatientStatus.Pending)
                ) {
                    visibleStatus = true
                }
                intentDone = true
                val intentView = Intent(this, ViewDetailsActivity::class.java)
                intentView.putExtra(
                    Constants.IntentKeyConstants.FIRST_NAME,
                    mConsultProvider?.getName()
                )
                intentView.putExtra(Constants.IntentKeyConstants.MESSAGE_OBJECT, mConsultMessage)
                intentView.putExtra(Constants.IntentKeyConstants.STR_UID, strUid.toString())
                intentView.putExtra(Constants.IntentKeyConstants.PATIENT_STATUS, visibleStatus)
                intentView.putExtra(Constants.IntentKeyConstants.MESSAGE_CHILD, MESSAGES_CHILD)
                intentView.putExtra(
                    Constants.IntentKeyConstants.CONSULT_PROVIDER,
                    mConsultProvider as Serializable?
                )
                startActivity(intentView)
            }
        }

        override fun onCancelled(error: DatabaseError) {

            dismissProgressBar()
        }
    }

    // For to wait till the send message API call to trigger and get response

    private val chatIsLive = false
    private var moveToChatIdPosition = 0
    fun getPatientDetails() {
        showProgressBar(
            PBMessageHelper().getMessage(
                this,
                Constants.API.getPatientDetails.toString()
            )
        )
        ChatActivityViewModel().getPatientDetails(
            getIntent().getStringExtra("consultProviderPatientId")?.toLong()
        )?.observe(this) { response ->

            dismissProgressBar()

            if (response != null && response.getStatus()) {
                val gson: Gson = Gson()

                showPatientRemovedDialog(response.patient.getStatus())
            } else if (!TextUtils.isEmpty(response.errorMsg) && response.errorMsg!= null) {
                showPatientRemovedDialog("Active")
            }
        }
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        //super.onCreate(savedInstanceState)
        containerParent = findViewById(R.id.container)
        ll_chat_popup = findViewById(R.id.ll_chat_popup)
        chatActBinding = DataBindingUtil.inflate(
            getLayoutInflater(),
            R.layout.activity_chat,
            containerParent,
            true
        )
        chatViewModel = ViewModelProvider(this).get(ChatActivityViewModel::class.java)
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        senderId = java.lang.String.valueOf(PrefUtility().getProviderId(this))

        chatActBinding!!.idsos.setOnClickListener(View.OnClickListener { handleSOSClick() })
        EncUtil().generateKey(this)


        encKey = PrefUtility().getAESAPIKey(this)

        MESSAGES_CHILD = getIntent().getStringExtra("path").toString() + "/messages"
        MEMBERS_CHILD = getIntent().getStringExtra("path").toString() + "/members"
        val UNREAD_CHILD: String = getIntent().getStringExtra("path").toString() + "/unread"

        AsyncTask.execute {
            //TODO your background code
            try {
                TrueTime().build().initialize()
            } catch (e: IOException) {

            }
        }
        initView()
    }

    private fun initView() {
        role = com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility().getRole(this)
        mUsername = com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility()
            .getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
        if (com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility()
                .getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
                .equals("RD")
        ) {
            mUsername = mUsername + ", " + com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility()
                .getStringInPref(
                this,
                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                ""
            )
        } else if (com.example.dailytasksamplepoc.kotlinomnicure.utils.PrefUtility()
                .getStringInPref(this, Constants.SharedPrefConstants.ROLE, "")
                .equals("BD")
        ) {
            if (currentUser?.getLcpType()
                    .equals(Constants.KeyHardcodeToken.LCP_TYPE_HOME,ignoreCase = true)
            ) {
                mUsername = "$mUsername, Homeside Provider"
            } else {
                mUsername = "$mUsername, Bedside Provider"
            }
        }
        setProviderObject()
        isInvited = getIntent().getBooleanExtra(Constants.IntentKeyConstants.INVITATION, false)
        isCompleted = getIntent().getBooleanExtra(Constants.IntentKeyConstants.COMPLETED, false)

//        Log.i(TAG, "initView: isCompleted " + isCompleted);
        handleChatEditView(isCompleted, isInvited)
        setToolbar()
        setPatientHistoryAdapter()
        patientChatHistory

        setupFirebaseAdapter()
        handleMenuItems()


        // Move to chat is false while initiated
        PrefUtility().saveStringInPref(
            this,
            Constants.SharedPrefConstants.MOVE_TO_CHAT_ID,
            ""
        )
        getTeamMemberDetails(mConsultProvider.getPatientsId(), strConsultTeamName)
        chatActBinding?.imgTeamCall.setOnClickListener(View.OnClickListener { v ->
            handleMultipleClick(v)

            if (membersList!!.size > 0 && membersList != null) {
                showTeamMembersDialog(this, membersList, mConsultProvider)
            } else {
                CustomSnackBar.make(
                    chatActBinding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    errorTeams,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
            }
        })

        // View details click listeners
        chatActBinding?.rlViewDetails.setOnClickListener { v ->
            // Expand if the view details drop down is clicked
            if (!expand) {
                chatActBinding?.imgViewDetailsArrow?.setImageResource(R.drawable.ic_updown_viewdetails)
                chatActBinding?.llExpandViewDetails?.setVisibility(View.VISIBLE)
                expand = true
            } else {
                // Collapse if the view is clicked again
                chatActBinding?.imgViewDetailsArrow?.setImageResource(R.drawable.ic_dropdown_viewdetails)
                chatActBinding?.llExpandViewDetails?.setVisibility(View.GONE)
                expand = false
            }
        }

        chatActBinding?.imgViewUrgentMsgs?.setOnClickListener { v ->
            // Handling the multi click
            handleMultipleClick(v)

            // Directing the user to View urgent messages activity with needed data
            val intentConsultChart: Intent =
                Intent(this, ViewUrgentMessagesActivity::class.java)
            intentConsultChart.putExtra("path", "consults/" + mConsultProvider.getId())
            val ph: PatientHistory?
            if (patientHistoryListBackup!!.size == 1) {
                val gson: Gson = Gson()
                ph = patientHistoryListBackup!!.get(0)

            } else {
                ph = patientHistory
            }

            if (ph == null) {
                CustomSnackBar.make(
                    chatActBinding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_messages_to_filter),
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
                return@setOnClickListener
            }
            intentConsultChart.putExtra("id", ph.getPatientId())
            intentConsultChart.putExtra("inviteTime", ph.getInviteTime())
            intentConsultChart.putExtra("status", ph.getStatus())
            intentConsultChart.putExtra("dischargeTime", ph.getDischargeTime())
            startActivity(intentConsultChart)
        }

        // Intent to View attachements message activity click listener
        chatActBinding?.imgViewAttachments.setOnClickListener { v ->


            // Handling the multi click
            handleMultipleClick(v)
            // Intent to View attachments
            val intent: Intent = Intent(this, ActivityAttachmentFilter::class.java)
            var ph: PatientHistory? = patientHistory
            if (patientHistoryListBackup!!.size == 1) {
                ph = patientHistoryListBackup!!.get(0)
            }
            if (ph == null) {
                CustomSnackBar.make(
                    chatActBinding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_messages_to_filter),
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
                return@setOnClickListener
            }
            //            System.out.println("patienthistorystr1 " + ph);
            intent.putExtra("inviteTime", ph!!.getInviteTime())
            intent.putExtra("dischargeTime", ph!!.getDischargeTime())
            intent.putExtra("status", ph!!.getStatus())
            intent.putExtra("messageChild", MESSAGES_CHILD)
            startActivity(intent)
        }
    }

    private fun setProviderObject() {
        mConsultProvider = ConsultProvider()
        mConsultProviderKey = getIntent().getStringExtra("consultProviderId")
        strConsultTeamName = getIntent().getStringExtra("teamNameConsult")
        strChartRemote = getIntent().getStringExtra("ConsultChartRemote")
        if (!TextUtils.isEmpty(strChartRemote) && strChartRemote == "chartRemote") {
            chatActBinding!!.imgTeamCall.setVisibility(View.INVISIBLE)
        } else {
            chatActBinding!!.imgTeamCall.setVisibility(View.VISIBLE)
        }
        strUid = getIntent().getLongExtra("uid", 0)

        mConsultProvider!!.setId(getIntent().getStringExtra("consultProviderPatientId")?.toLong())
        mConsultProvider!!.setPatientsId(
            getIntent().getStringExtra("consultProviderPatientId")?.toLong()
        )
        mConsultProvider!!.setText(getIntent().getStringExtra("consultProviderText"))
        mConsultProvider!!.setName(getIntent().getStringExtra("consultProviderName"))
        val dob: Long = getIntent().getLongExtra("dob", -1)
        mConsultProvider!!.setDob(dob)
        val gender: String = getIntent().getStringExtra("gender").toString()
        mConsultProvider!!.setGender(gender)
        val note: String = getIntent().getStringExtra("note").toString()
        mConsultProvider!!.setNote(note)
        val status: String = getIntent().getStringExtra("status").toString()
        if (!TextUtils.isEmpty(status)) {
            mConsultProvider!!.setStatus(Constants.PatientStatus.valueOf(status))
        }
        if (getIntent().getBooleanExtra(Constants.IntentKeyConstants.IS_PATIENT_URGENT, false)) {
            mConsultProvider!!.setUrgent(true)
        }
        if (getIntent().hasExtra("phone")) {
            mConsultProvider!!.setPhone(getIntent().getStringExtra("phone"))
        }
        if (mConsultProvider!!.getStatus().equals(Constants.PatientStatus.Invited) ||
            mConsultProvider!!.getStatus().equals(Constants.PatientStatus.Pending)
        ) {
            chatActBinding!!.imgTeamCall.setVisibility(View.INVISIBLE)
        }
    }

    private fun handleChatEditView(isCompleted: Boolean, isInvited: Boolean) {
        if (isCompleted && isInvited) {
            chatActBinding!!.linearLayout.setVisibility(View.GONE)
            chatActBinding!!.acceptBtn.setVisibility(View.VISIBLE)
        } else if (isCompleted) {
            chatActBinding!!.linearLayout.setVisibility(View.GONE)
            chatActBinding!!.acceptBtn.setVisibility(View.GONE)
        } else if (isInvited) {
            chatActBinding!!.linearLayout.setVisibility(View.GONE)
            chatActBinding!!.acceptBtn.setVisibility(View.VISIBLE)
        } else {
            chatActBinding!!.linearLayout.setVisibility(View.VISIBLE)
            chatActBinding!!.acceptBtn.setVisibility(View.GONE)
        }
        chatActBinding!!.linearLayout.post(Runnable {
            chatEditTextViewHeight = chatActBinding!!.linearLayout.getHeight()

        })
    }

    private fun setupFirebaseAdapter() {
        val uid: Long = PrefUtility().getLongInPref(this, Constants.SharedPrefConstants.USER_ID, 0)
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth!!.currentUser
        if (mFirebaseUser == null || uid == 0L) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        mProviderUid = PrefUtility().getFireBaseUid(this) //mFirebaseUser.getUid();

        // Initialize ProgressBar and RecyclerView.
        mMessageRecyclerView = findViewById(R.id.messageRecyclerView)
        mLinearLayoutManager = LinearLayoutManager(this)

        mMessageRecyclerView!!.layoutManager = mLinearLayoutManager
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference

        startReadListner()
        val parser: Any? =
            SnapshotParser<Any?> { dataSnapshot ->
                try {
                    val consultMessage: ConsultMessage? = dataSnapshot.getValue(ConsultMessage::class.java)

                    if (consultMessage != null) {
                        consultMessage.setId(dataSnapshot.key)

                    }
                    return@SnapshotParser consultMessage!!
                } catch (e: Exception) {

                }
                ConsultMessage()
            }
        val messagesRef = mFirebaseDatabaseReference!!.child(MESSAGES_CHILD!!)

        val topNBottomPadding: Int = UtilityMethods().dpToPx(8)
        val options: FirebaseRecyclerOptions<ConsultMessage> =
            FirebaseRecyclerOptions.Builder<ConsultMessage>().setQuery(messagesRef, parser).build()
        mFirebaseAdapter = object : FirebaseRecyclerAdapter<ConsultMessage, MessageViewHolder>(options) {
                override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MessageViewHolder {
                    val inflater = LayoutInflater.from(viewGroup.context)
                    // chat message
                    val binding: ItemMessageBinding =
                        DataBindingUtil.inflate(inflater, R.layout.item_message, viewGroup, false)
                    val viewHolder = MessageViewHolder(binding)
                    itemCount = mFirebaseAdapter!!.getItemCount()

                    return viewHolder
                }

                protected override fun onBindViewHolder(
                    viewHolder: MessageViewHolder,
                    position: Int,
                    consultMessage: ConsultMessage
                ) {


                    if (position == 0) {
                        viewHolder.getBinding().rootView.setPadding(0, topNBottomPadding, 0, 0)
                    } else if (position == mFirebaseAdapter!!.getItemCount() - 1) {
                        viewHolder.getBinding().rootView.setPadding(0, 0, 0, topNBottomPadding)
                    } else {
                        viewHolder.getBinding().rootView.setPadding(0, 0, 0, 0)
                    }
                    handleChatMessage(
                        consultMessage,
                        viewHolder.getBinding(),
                        position,
                        getRef(position)
                    )
                }
            }
        mFirebaseAdapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                val friendlyMessageCount = mFirebaseAdapter!!.getItemCount()
                val lastVisiblePosition =
                    mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                    positionStart >= friendlyMessageCount - 1 &&
                    lastVisiblePosition == positionStart - 1
                ) {
                    mMessageRecyclerView!!.scrollToPosition(positionStart)
                }

            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)

                val firebaseAdapterItemCount = mFirebaseAdapter!!.getItemCount()
                val lastVisiblePosition =
                    mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()

                mMessageRecyclerView!!.post {

                    mMessageRecyclerView!!.scrollToPosition(positionStart)
                }
            }
        })
        mMessageRecyclerView!!.adapter = mFirebaseAdapter
        mMessageRecyclerView!!.isNestedScrollingEnabled = false
        chatActBinding?.sendButton?.setEnabled(false)
        chatActBinding!!.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                chatActBinding!!.sendButton.setEnabled(
                    charSequence.toString().trim { it <= ' ' }.length > 0
                )
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        chatActBinding!!.sendButton.setOnClickListener(View.OnClickListener {
            val msg: String
            msg = AESUtils().encryptData(MessageType.text.toString(), encKey)


            val time: Long = getTime()
            val consultMessage = ConsultMessage(
                msg,
                mUsername,
                mPhotoUrl,
                null, "text", time, senderId, null, "Sent", role, 0L)

            //                Log.i(TAG, "Consult message " + new Gson().toJson(consultMessage));
            val ref = mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!)
                .push()
            consultMessage.setId(ref.key)
            ref.setValue(consultMessage)
            mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!).keepSynced(true)
            chatActBinding!!.messageEditText.text
            val asyncTask: AsyncTaskUpdateUnread = AsyncTaskUpdateUnread()
            asyncTask.execute(consultMessage)
        })
        chatActBinding!!.acceptBtn.setOnClickListener(View.OnClickListener {
            showProgressBar()
            chatActBinding!!.acceptBtn.setEnabled(false)
            if (mConsultProvider?.getStatus() === Constants.PatientStatus.Invited) {
                val providerId: Long = PrefUtility().getProviderId(this@ChatActivity)
                val token: String? = PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.TOKEN,
                    ""
                )
                viewModel?.acceptInvite(providerId, token, mConsultProvider?.getPatientsId())
                    ?.observe(this) { listResponse ->
                        chatActBinding!!.acceptBtn.setEnabled(true)
                        if ((listResponse != null) && (listResponse.status != null) && listResponse.status) {
                            //                            Log.d(TAG, "acceptInvite listResponse : " + new Gson().toJson(listResponse));
                            if (mConsultProvider != null) {
                                mConsultProvider!!.setStatus(Constants.PatientStatus.Active)
                                chatActBinding!!.imgTeamCall.setVisibility(View.VISIBLE)
                                handleMenuItems()
                            }
                            handleChatEditView(false, false)
                            getPatientDetails((strUid)!!)
                        } else {
                            dismissProgressBar()
                            val errMsg: String? = ErrorMessages().getErrorMessage(
                                this,
                                listResponse.errorMessage,
                                Constants.API.acceptInvite
                            )

                            CustomSnackBar.make(
                                chatActBinding.getRoot(), this@ChatActivity,
                                CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0
                            )?.show()
                        }
                    }
            } else {
                val uid: Long = PrefUtility().getLongInPref(
                    this,
                    Constants.SharedPrefConstants.USER_ID,
                    0
                )
                val handOffAcceptRequest = HandOffAcceptRequest()
                handOffAcceptRequest.setPatientId(mConsultProvider.getId())
                handOffAcceptRequest.setProviderId(uid)

                viewModel?.acceptRemoteHandoff(handOffAcceptRequest)
                    ?.observe(this) { listResponse ->
                        if ((listResponse != null) && (listResponse.status != null) && listResponse.status) {

                            if (mConsultProvider != null) {
                                mConsultProvider!!.setStatus(Constants.PatientStatus.Active)
                                chatActBinding!!.imgTeamCall.setVisibility(View.VISIBLE)
                                handleMenuItems()
                            }
                            handleChatEditView(false, false)
                            getPatientDetails((strUid)!!)
                        } else {
                            dismissProgressBar()
                            val errMsg: String = ErrorMessages().getErrorMessage(
                                this,
                                listResponse.getErrorMessage(),
                                Constants.API.acceptInvite
                            )

                            CustomSnackBar.make(
                                chatActBinding.getRoot(), this,
                                CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0
                            )?.show()
                        }
                    }
            }
        })
        chatActBinding!!.addMessageImageView.setEnabled(true)
        chatActBinding!!.addMessageImageView.setOnClickListener(View.OnClickListener { showAttachmentPopup() })
        findMembers()

        //startUnreadResetListner();
        detectKeyboard()
        mFirebaseAdapter!!.startListening()
    }

    fun acceptFailureMessage(errorMessage: String?) {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val dialogView: View = LayoutInflater.from(getApplicationContext())
            .inflate(R.layout.custom_alert_dialogs, viewGroup, false)
        val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
        val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
        alertMsg.text = errorMessage
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        buttonOk.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        alertDialog.show()
    }

    private fun startUnreadResetListner() {
//        Log.d(TAG, "startUnreadResetListner: " + mConsultProviderKey);
        mUnreadResetDB =
            mFirebaseDatabaseReference!!.child("providers").child((mProviderUid)!!).child("active")
                .child(
                    (mConsultProviderKey)!!
                )
        mUnreadResetDB!!.addValueEventListener((mUnreadResetListner)!!)
    }

    private fun stopUnreadResetListner() {
        if (mUnreadResetDB != null && mUnreadResetListner != null) {
            mUnreadResetDB!!.removeEventListener(mUnreadResetListner!!)
        }
    }

    private fun startReadListner() {
//
        mreadDB = mFirebaseDatabaseReference!!.child("consults").child(strUid.toString())
            .child("messages")
        mreadDB!!.addValueEventListener((mrReadListener)!!)
    }

    private fun stopReadListner() {
        if (mreadDB != null && mrReadListener != null) {
            mreadDB!!.removeEventListener(mrReadListener!!)
        }
    }

    // Start the view details listener
    private fun startViewDetailsListener(consultMsgId: String, strUid: Long) {
//
        //Starting the value event listener for view details
//      ist");
        mViewDetailsListenerDataRef =
            mFirebaseDatabaseReference!!.child("consults").child(strUid.toString())
                .child("messages")
                .child(consultMsgId).child("ChatMessageStatusList")
        mViewDetailsListenerDataRef!!.addValueEventListener((mViewDetailsValueEventListener)!!)
    }

    //Stopping the view details listener

        if (mViewDetailsListenerDataRef != null && mViewDetailsValueEventListener != null) {
            mViewDetailsListenerDataRef!!.removeEventListener(mViewDetailsValueEventListener!!)
        }
    }

    private fun chatItemClicK(path: String) {
        mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!).child(path)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val consultMessageA: ConsultMessage? =
                        dataSnapshot.getValue(ConsultMessage::class.java)
                    if (consultMessageA.getImageUrl() == null) {
                        return
                    }
                    val storagePermission = ActivityCompat.checkSelfPermission(
                        this@ChatActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    if (storagePermission == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(
                            this@ChatActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            Constants.PermissionCondes.STORAGE_PERMISSION_CODE
                        )
                        return
                    }
                    val file = consultMessageA?.let { getFile(it) }
                    if (file != null) {
                        if (!file.exists()) {
                            mFileDownloadTask =
                                consultMessageA?.let { TaskConsultMessage(it, file.absolutePath) }
                            mFileDownloadTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                        } else {
                            consultMessageA.setImageUrl(file.absolutePath)
                            processDownloadedFile(consultMessageA)
                        }
                    } else {
//                            UtilityMethods.showErrorSnackBar(chatActBinding.getRoot(), getString(R.string.file_delete_err_msg), Snackbar.LENGTH_LONG);
                        CustomSnackBar.make(
                            chatActBinding?.getRoot(),
                            this,
                            CustomSnackBar.WARNING,
                            getString(R.string.file_delete_err_msg),
                            CustomSnackBar.TOP,
                            3000,
                            0
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.PermissionCondes.STORAGE_PERMISSION_CODE -> {
                val isGranted: Boolean = UtilityMethods().checkPermission(this, permissions)
                if (isGranted) {
                    //TODO
                } else {
                    val permissonErr: String = getString(R.string.permission_denied)
                    CustomSnackBar.make(
                        binding?.container,
                        this,
                        CustomSnackBar.WARNING,
                        permissonErr,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
            Constants.PermissionCondes.STORAGE_PERMISSION_CODE_DOCUMENT -> {
                val isGranted: Boolean = UtilityMethods().checkPermission(this, permissions)
                if (isGranted) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "*/*"
                    val mimetypes = arrayOf(
                        "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.ms-powerpoint"
                    )
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                    startActivityForResult(intent, REQUEST_FILE)
                } else {
                    val permissonErr: String = getString(R.string.permission_denied)
                    CustomSnackBar.make(
                        binding?.container,
                        this,
                        CustomSnackBar.WARNING,
                        permissonErr,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
            Constants.PermissionCondes.STORAGE_PERMISSION_CODE_VIDEO -> {
                val isGranted: Boolean = UtilityMethods().checkPermission(this, permissions)
                if (isGranted) {
                    val pickVideo = Intent(Intent.ACTION_PICK)
                    pickVideo.type = "video/*"
                    startActivityForResult(pickVideo, REQUEST_VIDEO)
                } else {
                    val permissonErr: String = getString(R.string.permission_denied)
                    CustomSnackBar.make(
                        binding.container,
                        this,
                        CustomSnackBar.WARNING,
                        permissonErr,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
            Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE -> {
                val isGranted: Boolean = UtilityMethods().checkPermission(this, permissions)
                if (isGranted) {
                    GeeksMediaPicker.with(this)
                        .setEnableCompression(true)
                        .startCamera { mediaStoreData ->
                            val filePath: String = mediaStoreData.getMedia_path()
                            val file: File = File(filePath)

                            sendImage(Uri.fromFile(file))
                            null
                        }


                } else {
                    val permissonErr: String = getString(R.string.permission_denied)

                    CustomSnackBar.make(
                        binding.container,
                        this,
                        CustomSnackBar.WARNING,
                        permissonErr,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    ).show()
                }
            }
            Constants.PermissionCondes.ONLY_STORAGE_PERMISSION_CODE -> {
                val isGranted: Boolean = UtilityMethods.checkPermission(this, permissions)
                if (isGranted) {
                    /*Intent pickPhoto = new Intent(ChatActivity.this,ImageCaptureActivity.class);
                    pickPhoto.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, Constants.ImageCaptureConstants.OPEN_MEDIA);
                    pickPhoto.putExtra(Constants.ImageCaptureConstants.SOURCE, ChatActivity.class.getSimpleName());
                    startActivityForResult(pickPhoto,REQUEST_IMAGE);*/
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, REQUEST_IMAGE)
                } else {
                    val permissonErr: String = getString(R.string.permission_denied)
                    //                    UtilityMethods.showErrorSnackBar(binding.container, permissonErr, Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        binding.container,
                        this,
                        CustomSnackBar.WARNING,
                        permissonErr,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    ).show()
                }
            }
        }
    }

    private fun getFile(consultMessage: ConsultMessage): File? {
        try {
            val filePath: String = consultMessage.getImageUrl()
            if (consultMessage.getImageUrl().contains("?alt=media&")) {
                val arr: List<String> = consultMessage.getImageUrl().split("\\?alt=media&")
                val ur = arr[0]
                val currentURL = FIREBASE_STORAGE_URL + BuildConfigConstants.baseUrl.toString() + "/o/"
                if (ur.startsWith(currentURL)) {
                    val p = ur.replace(currentURL, "")
                    try {
                        val pth = URLDecoder.decode(p, "UTF-8")
                        var folderPath = ""
                        var filename: String = consultMessage.getFilename()
                        if (pth.contains("/")) {
                            val i = pth.lastIndexOf("/")
                            val a = arrayOf(pth.substring(0, i), pth.substring(i + 1))
                            folderPath = File.separator + a[0]
                            if ((TextUtils.isEmpty(filename) && (a.size > 1) && !TextUtils.isEmpty(a[1])) || filename.contains(
                                    "/"
                                )
                            ) {
                                filename = a[1]
                            }
                        }
                        /*if(filename.contains("/")){
                            int i = filename.lastIndexOf("/");
                            filename =filename.substring(i);
                        }*/
                        val folder = File(
                            (Environment.getExternalStorageDirectory().toString() +
                                    File.separator + Constants.APP_NAME + folderPath)
                        )
                        val file = File(folder, filename)
                        if (!file.exists()) {
                            if (!folder.exists()) {
                                val st = folder.mkdirs()
                            }
                        }

                        return file
                    } catch (e: UnsupportedEncodingException) {

                    }
                }
            }
        } catch (e: Exception) {

        }
        return null
    }

    private fun processDownloadedFile(consultMessageA: ConsultMessage?) {
        if (consultMessageA == null) {
            return
        }
        try {
            if ("image".equals(consultMessageA.getType(), ignoreCase = true)) {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                var mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(fileExt(consultMessageA.getImageUrl()))
                if (TextUtils.isEmpty(mimeType)) {
                    mimeType = "image/*"
                }
                val authority = BuildConfig.APPLICATION_ID + "." + Constants.FILE_PROVIDER_NAME
                val uri =
                    FileProvider.getUriForFile(this, authority, File(consultMessageA.getImageUrl()))
                intent.setDataAndType(uri, mimeType)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            } else if ("file".equals(consultMessageA.getType(), ignoreCase = true)) {

                openFileContent(consultMessageA)
            } else if ("video".equals(consultMessageA.getType(), ignoreCase = true)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(consultMessageA.getImageUrl()))
                intent.setDataAndType(Uri.parse(consultMessageA.getImageUrl()), "video/mp4")
                startActivity(intent)
            }
        } catch (e: Exception) {

            CustomSnackBar.make(
                chatActBinding.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_handler_for_file_error),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
        }
    }

    private fun openFileContent(consultMessage: ConsultMessage?) {
        try {
            if (consultMessage == null) {
//                UtilityMethods.showErrorSnackBar(chatActBinding.getRoot(), getString(R.string.no_content_found), Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    chatActBinding.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_content_found),
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
                return
            }

            val intent = Intent(Intent.ACTION_VIEW)
            var mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(fileExt(consultMessage.getImageUrl()))
            //            Log.d(TAG, "openFileContent: Mime Type : " + mimeType);
            if (TextUtils.isEmpty(mimeType)) {
                mimeType = getExtension(consultMessage.getFilename())
                //                Log.d(TAG, "Mime Type : " + mimeType);
            }
            val authority = BuildConfig.APPLICATION_ID + "." + Constants.FILE_PROVIDER_NAME
            //            Log.d(TAG, "openFileContent: authority : " + authority);
            val uri =
                FileProvider.getUriForFile(this, authority, File(consultMessage.getImageUrl()))
            intent.setDataAndType(uri, mimeType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {


            CustomSnackBar.make(
                chatActBinding.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_handler_for_file_error),
                CustomSnackBar.TOP,
                3000,
                0
            ).show()
        } catch (e: Exception) {
//
        }
    }

    private fun fileExt(url: String): String {
        var url: String? = url
        if (url == null) {
            return ""
        }
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"))
        }
        if (url.lastIndexOf(".") == -1) {
            return ""
        } else {
            var ext = url.substring(url.lastIndexOf(".") + 1)
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"))
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"))
            }
            return ext.toLowerCase()
        }
    }

    fun getExtension(fileName: String?): String {
        if (fileName == null) {
            return ""
        }
        if (fileName.contains(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        } else if (fileName.contains(".doc")) {
            return "application/msword"
        } else if (fileName.contains(".pdf")) {
            return "application/pdf"
        } else if (fileName.contains(".pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        } else if (fileName.contains(".ppt")) {
            return "application/vnd.ms-powerpoint"
        } else if (fileName.contains(".xls") || fileName.contains(".xlsx")) {
            return "application/vnd.ms-excel"
        } else if (fileName.contains(".zip") || fileName.contains(".rar")) {
            return "application/x-wav"
        } else if (fileName.contains(".rtf")) {
            return "application/rtf"
        } else if (fileName.contains(".wav") || fileName.contains(".mp3")) {
            return "audio/x-wav"
        } else if (fileName.contains(".gif")) {
            return "image/gif"
        } else if (fileName.contains(".jpg") || fileName.contains(".jpeg") || fileName.contains(".png") || fileName.contains(
                ".webp"
            )
        ) {
            return "image/jpeg"
        } else if (fileName.contains(".txt")) {
            return "text/plain"
        } else return if (fileName.contains(".mov") || fileName.contains(".mp4")) {
            "video/mp4"
        } else {
            "*/*"
        }
    }

    private fun downloadFile(url: String, outputFile: String) {
        if (!UtilityMethods().isInternetConnected(this)) {

            CustomSnackBar.make(
                chatActBinding.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }

        var conn: HttpURLConnection? = null
        var stream: DataInputStream? = null
        var fos: DataOutputStream? = null
        try {
            val u = URL(url)
            conn = u.openConnection() as HttpURLConnection
            conn!!.connect()
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            val contentLength = conn.contentLength
            stream = DataInputStream(u.openStream())
            fos = DataOutputStream(FileOutputStream("$outputFile.part"))
            val buffer = ByteArray(contentLength)
            var total = 0
            var count: Int
            while ((stream.read(buffer).also { count = it }) != -1) {
                total = total + count
                if (contentLength > 0) {
                    val finalTotal = total
                    mHandler.post(Runnable {
                        val progress = (finalTotal * 100) / contentLength
                        if (downloadPB != null && downloadPB.isShowing()) {
                            downloadPB.setProgress(progress)
                        }
                    })
                }
                fos.write(buffer, 0, count)
            }
            fos.flush()
            fos.close()
            //Move
            moveFile("$outputFile.part", outputFile)
        } catch (e: FileNotFoundException) {

            dismissProgressBar()
            return
        } catch (e: Exception) {

            dismissProgressBar()
            return
        } finally {
            conn?.disconnect()
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
//
                }
            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
//
                }
            }
        }
    }

    private fun moveFile(inputPath: String, outputPath: String): Boolean {
//        Log.d(TAG, "moveFile: " + inputPath + ", " + outputPath);
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = FileInputStream(inputPath)
            out = FileOutputStream(outputPath)
            val buffer = ByteArray(1024)
            var read: Int
            while ((`in`.read(buffer).also { read = it }) != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()
            `in` = null

            // write the output file
            out.flush()
            out.close()
            out = null

            // delete the original file
            return (File(inputPath)).delete()
        } catch (e: FileNotFoundException) {
//            Log.e(TAG, "Exception:", e.getCause());
            dismissProgressBar()
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
            dismissProgressBar()
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
//                    Log.e(TAG, "Exception:", e.getCause());
                }
            }
            if (out != null) try {
                out.close()
            } catch (e: IOException) {
//                    Log.e(TAG, "Exception:", e.getCause());
            }
        }
        return false
    }

    private fun updateMessageStatus(path: String?) {
        mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!).child((path)!!).child("status")
            .setValue("Read")
    }

    private fun showAttachmentPopup() {
        if (popup != null && popup.isShowing()) {
            popup.onDismiss()
            return
        }
     popup = ChatAttachmentPopup(this)
        val delay = 500
        val documentListener = label@ View.OnClickListener { view: View? ->
            popup.onDismiss()
            val storagePermission: Int = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (storagePermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constants.PermissionCondes.STORAGE_PERMISSION_CODE_DOCUMENT
                )
                return@label
            }
            mHandler.postDelayed(object : Runnable {
                override fun run() {
                    val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("*/*")
                    val mimetypes: Array<String> = arrayOf(
                        "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.ms-powerpoint"
                    )
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                    startActivityForResult(intent, REQUEST_FILE)
                }
            }, delay)
        }
        val cameraListener = label@ View.OnClickListener { view: View? ->
            popup.onDismiss()
            val cameraPermission: Int =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            val storagePermission: Int = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (cameraPermission == PackageManager.PERMISSION_DENIED || storagePermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constants.PermissionCondes.CAMERA_STORAGE_PERMISSION_CODE
                )
                return@label
            }
            mHandler.postDelayed(object : Runnable {
                override fun run() {
                    GeeksMediaPicker.with(this@ChatActivity)
                        .setEnableCompression(true)
                        .startCamera { mediaStoreData ->
                            val filePath: String = mediaStoreData.getMedia_path()
                            val file: File = File(filePath)
                            sendImage(Uri.fromFile(file))
                            null
                        }
                    /* Intent intent = new Intent(ChatActivity.this, ImageCaptureActivity.class);
         intent.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, Constants.ImageCaptureConstants.OPEN_CAMERA);
         intent.putExtra(Constants.ImageCaptureConstants.SOURCE, ChatActivity.class.getSimpleName());
         startActivityForResult(intent, REQUEST_CAMERA);*/
                }
            }, delay)
        }
        val galleryListener = label@ View.OnClickListener { view: View? ->
            popup.onDismiss()
            val storagePermission: Int = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (storagePermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.PermissionCondes.ONLY_STORAGE_PERMISSION_CODE
                )
                return@label
            }
            mHandler.postDelayed(object : Runnable {
                override fun run() {
                    /*Intent pickPhoto = new Intent(ChatActivity.this,ImageCaptureActivity.class);
                    pickPhoto.putExtra(Constants.ImageCaptureConstants.OPEN_INTENT_PREFERENCE, Constants.ImageCaptureConstants.OPEN_MEDIA);
                    pickPhoto.putExtra(Constants.ImageCaptureConstants.SOURCE, ChatActivity.class.getSimpleName());
                    startActivityForResult(pickPhoto,REQUEST_IMAGE);*/
                    val pickPhoto: Intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhoto, REQUEST_IMAGE)
                }
            }, delay)
        }
        val videoListener = label@ View.OnClickListener { view: View? ->
            popup.onDismiss()
            val storagePermission: Int = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (storagePermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constants.PermissionCondes.STORAGE_PERMISSION_CODE_VIDEO
                )
                return@label
            }
            mHandler.postDelayed({
                val pickVideo: Intent = Intent(Intent.ACTION_PICK)
                pickVideo.setType("video/*")
                startActivityForResult(pickVideo, REQUEST_VIDEO)
            }, delay)
        }

        popup.setDocumentClickListener(documentListener)
        popup.setCameraClicListener(cameraListener)
        popup.setGalleryClicListener(galleryListener)
        popup.setVideoClickListener(videoListener)
        popup.initConfig()
        popup.show()
    }

    fun findMembers() {

        mFirebaseDatabaseReference!!.child((MEMBERS_CHILD)!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null && dataSnapshot.value is Map<*, *>) {
                        membersMap = dataSnapshot.value as MutableMap<*, *>?

                    } else if (dataSnapshot.value != null && dataSnapshot.value is ArrayList<*>) {
                        val membersMapList: List<String>? = dataSnapshot.value as ArrayList<*>?
                        membersMap = HashMap()
                        if (membersMapList != null && membersMapList.size > 0) {
                            var i = 0
                            for (v: String? in membersMapList) {
                                if (v != null) {
                                    membersMap["" + i] = v
                                }
                                i += 1
                            }
                        }
                        for (s: String? in membersMap.keys) {
//                        Log.d(TAG, s + " : " + membersMap.get(s));
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
//                Log.d(TAG, "onCancelled: " + databaseError);
                }
            })
    }

    fun onStart() {
        super.onStart()
        try {
            startUnreadResetListner()
            (getApplicationContext() as OmnicureApp).setCurrentContext(this)
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    fun onPause() {
        super.onPause()
    }

    fun onResume() {
        super.onResume()
        intentDone = false
        moveToChatId = PrefUtility.getStringInPref(
            thi,
            com.mvp.omnicure.utils.Constants.SharedPrefConstants.MOVE_TO_CHAT_ID,
            ""
        )
        //        Log.e(TAG, "onResume:moveToChatId--> " + moveToChatId);
        if (moveToChatId != null && !moveToChatId!!.isEmpty()) {
//            Log.e(TAG, "onResume:moveToChatIdchatCount--> " + moveToChatHashMap.values().size());
            for (consultMessage: Map.Entry<Int, ConsultMessage> in moveToChatHashMap.entries) {
                if (consultMessage.value.getId().equalsIgnoreCase(moveToChatId)) {
                    moveToChatIdPosition = consultMessage.key
                    //                    Log.e(TAG, "onResume:moveToChatIdPosition--> " + moveToChatIdPosition);
//                    Log.e(TAG, "onResume:moveToChatIditemCount--> " + mFirebaseAdapter.getItemCount());
                    if (consultMessage.value.getType()
                            .equalsIgnoreCase(MessageType.text.toString())
                    ) {
//                        Log.e(TAG, "onResume:moveToChatIdText--> " + AESUtils.decryptData(consultMessage.getValue().getText().trim(), encKey));
                    }

                    // Scroll to specific position using scroll view
                    val y = mMessageRecyclerView!!.y + mMessageRecyclerView!!.getChildAt(
                        moveToChatIdPosition
                    ).y
                    //                    Log.e(TAG, "onResume: yValue-->" + (int) y);
                    chatActBinding.scrollView.smoothScrollTo(0, y.toInt())

                    // Show animation for move to chat item from urgent message page
                    val v = mMessageRecyclerView!!.getChildAt(moveToChatIdPosition)
                    v.alpha = 0.0f
                    v.animate().alpha(1.0f)
                        .setDuration(1000)
                        .setStartDelay(50)
                        .start()
                }
            }
        }
        clearNotifications(Constants.NotificationIds.MSG_NOTIFICATION_ID)
    }

    /*    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        handleMenuItems();
        return true;
    }*/
    protected fun onStop() {
        super.onStop()
        try {
            (getApplicationContext() as OmnicureApp).setCurrentContext(null)
            stopUnreadResetListner()
            stopReadListner()
            // Stopping the view details listener
            stopViewDetailsListener()
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getCurrentUser() != null) {
            if (!TextUtils.isEmpty(getCurrentUser().getLcpType())) {
                if (getCurrentUser().getLcpType().equalsIgnoreCase(Constants.KeyHardcodeToken.LCP_TYPE_HOME)) {
                    menu.findItem(R.id.action_transfer_patient).setVisible(true);
                    menu.findItem(R.id.action_complete).setVisible(false);
                    menu.findItem(R.id.action_handoff).setVisible(false);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
*/
    fun onDestroy() {
        super.onDestroy()
        //unregisterBroadcastReceiver();
        try {
            stopUnreadResetListner()
            mFirebaseAdapter!!.stopListening()
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    /*
      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          switch (item.getItemId()) {
  //            case R.id.sign_out_menu:
  //                mFirebaseAuth.signOut();
  //                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
  //                mUsername = ANONYMOUS;
  //                startActivity(new Intent(this, LoginActivity.class));
  //                finish();
  //                return true;
              case R.id.audio_call:
                  groupCall(Constants.FCMMessageType.AUDIO_CALL);
  //                startCall(Constants.FCMMessageType.AUDIO_CALL);

                  return true;
              case R.id.action_call:
                  groupCall(Constants.FCMMessageType.VIDEO_CALL);
  //                startCall(Constants.FCMMessageType.VIDEO_CALL);

                  return true;
              case R.id.invite_menu:
                  inviteProvider();
  //                onClickDirectory();
                  return true;
              case R.id.action_patient_detail:
                  //TODO: add code here
              {
                  Intent i = new Intent(ChatActivity.this, PatientDetailActivity.class);
                  i.putExtra("uid", mConsultProvider.getId());
                  i.putExtra("name", mConsultProvider.getName());
                  i.putExtra("dob", mConsultProvider.getDob());
                  i.putExtra("gender", mConsultProvider.getGender());
                  i.putExtra("phone", mConsultProvider.getPhone());
                  i.putExtra("note", mConsultProvider.getNote());
                  startActivity(i);
              }
              return true;

              case R.id.action_transfer_patient:
                  Intent intent = new Intent(ChatActivity.this, TransferPatientActivity.class);
                  intent.putExtra("patientId", mConsultProvider.getPatientId());
                  startActivity(intent);
                  return true;
              case R.id.action_complete:
                  doDischargePatient();
                  return true;
              case R.id.action_handoff:
                  doRemoteSideHandOff();
                  return true;
              case R.id.complete_consultation: {
                  doCompleteConsultation();
                  return true;
              }
              default:
                  return super.onOptionsItemSelected(item);
          }
          return super.onPrepareOptionsMenu(menu);
      }
  */
    private fun handleMenuItems() {
        if (menu == null) {
            return
        }
        audioCall = menu.findItem(R.id.audio_call)
        videoIcon = menu.findItem(R.id.action_call)
        directoryMenu = menu.findItem(R.id.invite_menu)
        markCompleteMenu = menu.findItem(R.id.action_complete)
        transferMenu = menu.findItem(R.id.action_transfer_patient)
        handOffPatient = menu.findItem(R.id.action_handoff)
        transferPatient = menu.findItem(R.id.action_transfer_patient)
        patientDetails = menu.findItem(R.id.action_patient_detail)
        completeConsultation = menu.findItem(R.id.complete_consultation)
        val role: String = PrefUtility.getRole(this)
        patientstatus = mConsultProvider.getStatus()

//        System.out.println("patient details " + role + " " + mConsultProvider.getStatus() + " " + PrefUtility.getProviderId(ChatActivity.this));
        var isPatientDischarged = false
        var isPatientInvited = false
        var isPatientCompleted = false
        var isPatientNew = false
        var isPatientActive = false
        var isPatientHomeCare = false
        var isHandoffPending = false
        if (mConsultProvider != null && mConsultProvider.getStatus() != null) {
            isPatientDischarged =
                mConsultProvider.getStatus().equals(Constants.PatientStatus.Discharged)
            isPatientNew = mConsultProvider.getStatus().equals(Constants.PatientStatus.New)
            isPatientCompleted =
                mConsultProvider.getStatus().equals(Constants.PatientStatus.Completed)
            isPatientHomeCare =
                mConsultProvider.getStatus().equals(Constants.PatientStatus.HomeCare)
            isHandoffPending =
                mConsultProvider.getStatus().equals(Constants.PatientStatus.HandoffPending)
            isPatientInvited = (mConsultProvider.getStatus().equals(Constants.PatientStatus.Invited)
                    || mConsultProvider.getStatus()
                .equals(Constants.PatientStatus.Pending) || mConsultProvider.getStatus()
                .equals(Constants.PatientStatus.Handoff))
            isPatientActive = mConsultProvider.getStatus().equals(Constants.PatientStatus.Active)
        }
        if (role.equals(Constants.ProviderRole.RD.toString(), ignoreCase = true)) {
            directoryMenu.setVisible(false)
            markCompleteMenu.setVisible(false)
            transferPatient.setVisible(false)
            chatActBinding.idsos.setVisibility(View.GONE)
            if (isPatientDischarged) {
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                hideMenu()
                patientDetails.setVisible(true)
            } else if (isPatientInvited) {
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                hideMenu()
                patientDetails.setVisible(true)
            } else if (isHandoffPending) {
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                hideMenu()
                patientDetails.setVisible(true)
                completeConsultation.setVisible(true)
            } else if (isPatientCompleted) {
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                hideMenu()
                patientDetails.setVisible(true)
                //                handOffPatient.setVisible(true);
            } else {
                /*   audioCall.setVisible(true);
                videoIcon.setVisible(true);*/
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                patientDetails.setVisible(true)
                handOffPatient.setVisible(true)
                completeConsultation.setVisible(true)
                val strRpUserType: String = PrefUtility.getStringInPref(
                    this,
                    Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                    ""
                )
                if ((strRpUserType == "MD/DO")) {
                    handOffPatient.setVisible(true)
                    completeConsultation.setVisible(true)
                } else {
                    handOffPatient.setVisible(false)
                    completeConsultation.setVisible(false)
                }
            }
        } else {
            if (isPatientDischarged) {
                chatActBinding.linearLayout.setVisibility(View.GONE)
                chatActBinding.idsos.setVisibility(View.GONE)
                videoIcon.setVisible(false)
                audioCall.setVisible(false)
                transferMenu.setVisible(false)
                hideMenu()
                patientDetails.setVisible(true)
            } else if (isPatientInvited) {
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                directoryMenu.setVisible(false)
                handOffPatient.setVisible(false)
                //                chatActBinding.idsos.setVisibility(View.VISIBLE);
                chatActBinding.idsos.setVisibility(View.GONE)
                markCompleteMenu.setVisible(false)
            } else if (isPatientCompleted) {
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                hideMenu()
                patientDetails.setVisible(true)
                transferMenu.setVisible(true)
                directoryMenu.setVisible(true)
                markCompleteMenu.setVisible(true)
            } else if (isPatientNew) {
                hideMenu()
                directoryMenu.setVisible(true)
            } else if (isPatientHomeCare) {
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                hideMenu()
                directoryMenu.setVisible(true)
                patientDetails.setVisible(true)
            } else {
                /*   audioCall.setVisible(true);
                videoIcon.setVisible(true);*/
                audioCall.setVisible(false)
                videoIcon.setVisible(false)
                directoryMenu.setVisible(false)
                handOffPatient.setVisible(false)
                //                chatActBinding.idsos.setVisibility(View.VISIBLE);
                chatActBinding.idsos.setVisibility(View.GONE)
                markCompleteMenu.setVisible(false)
            }
            completeConsultation.setVisible(false)
            handOffPatient.setVisible(false)

            /*  if (mConsultProvider.getStatus().equals(Constants.PatientStatus.HomeCare)) {
                audioCall.setVisible(false);
                videoIcon.setVisible(false);
                directoryMenu.setVisible(true);
                patientDetails.setVisible(true);
                transferPatient.setVisible(true);
                markCompleteMenu.setVisible(true);
            } else if (isPatientCompleted) {
                audioCall.setVisible(false);
                videoIcon.setVisible(false);
                hideMenu();
                patientDetails.setVisible(true);
                transferMenu.setVisible(true);
                directoryMenu.setVisible(true);
                markCompleteMenu.setVisible(true);
            }*/
        }

//        SpannableString s = new SpannableString("Send S.O.S.");
//        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
//        sosMenu.setTitle(s);
    }

    private fun hideMenu() {
        patientDetails!!.isVisible = false
        markCompleteMenu!!.isVisible = false
        directoryMenu!!.isVisible = false
        transferMenu!!.isVisible = false
        handOffPatient!!.isVisible = false
        transferPatient!!.isVisible = false
        completeConsultation!!.isVisible = false
        handOffPatient!!.isVisible = false
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.sign_out_menu:
//                mFirebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                mUsername = ANONYMOUS;
//                startActivity(new Intent(this, LoginActivity.class));
//                finish();
//                return true;
            case R.id.audio_call:
                groupCall(Constants.FCMMessageType.AUDIO_CALL);
//                startCall(Constants.FCMMessageType.AUDIO_CALL);

                return true;
            case R.id.action_call:
                groupCall(Constants.FCMMessageType.VIDEO_CALL);
//                startCall(Constants.FCMMessageType.VIDEO_CALL);

                return true;
            case R.id.invite_menu:
                inviteProvider();
//                onClickDirectory();
                return true;
            case R.id.action_patient_detail:
                //TODO: add code here
            {
                Intent i = new Intent(ChatActivity.this, PatientDetailActivity.class);
                i.putExtra("uid", mConsultProvider.getId());
                i.putExtra("name", mConsultProvider.getName());
                i.putExtra("dob", mConsultProvider.getDob());
                i.putExtra("gender", mConsultProvider.getGender());
                i.putExtra("phone", mConsultProvider.getPhone());
                i.putExtra("note", mConsultProvider.getNote());
                startActivity(i);
            }
            return true;

            case R.id.action_transfer_patient:
                Intent intent = new Intent(ChatActivity.this, TransferPatientActivity.class);
                intent.putExtra("patientId", mConsultProvider.getPatientId());
                startActivity(intent);
                return true;
            case R.id.action_complete:
                doDischargePatient();
                return true;
            case R.id.action_handoff:
                doRemoteSideHandOff();
                return true;
            case R.id.complete_consultation: {
                doCompleteConsultation();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
    fun groupCall(callType: String?) {
        showProgressBar()
        val providerID: Long = PrefUtility.getProviderId(this@ChatActivity)
        val token: String =
            PrefUtility.getStringInPref(this@ChatActivity, Constants.SharedPrefConstants.TOKEN, "")
        val content = GroupCall()
        val receiverIds = ArrayList<String>()
        content.setId(providerID.toString())
        content.setMessage("")
        content.setPatientsId(java.lang.String.valueOf(mConsultProvider.getPatientsId()))
        content.setToken(token)
        content.setType(callType)
        //        content.setReceiverIds(receiverIds.toArray(new String[receiverIds.size()]));
        chatViewModel.multipleCall(content).observe(this@ChatActivity) { commonResponse ->
            dismissProgressBar()
            if ((commonResponse != null) && (commonResponse.getStatus() != null) && commonResponse.getStatus()) {
                val callScreen: Intent = Intent(this@ChatActivity, CallActivity::class.java)
                callScreen.putExtra(
                    "providerName",
                    PrefUtility.getStringInPref(
                        this@ChatActivity,
                        Constants.SharedPrefConstants.NAME,
                        ""
                    )
                )
                callScreen.putExtra(
                    "providerHospitalName",
                    PrefUtility.getStringInPref(
                        this@ChatActivity,
                        Constants.SharedPrefConstants.HOSPITAL_NAME,
                        ""
                    )
                )

//                callScreen.putExtra("providerId", provider.getId());
//                String chName = "";
//                for(int i=0; i< commonResponse.getProviderList().size(); i++){
//                    Provider pr = commonResponse.getProviderList().get(i);
//                    chName = "-"+pr.getId();
//                }
//                callScreen.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, mConsultProvider.getId()+chName);
                callScreen.putExtra(
                    ConstantApp.ACTION_KEY_CHANNEL_NAME,
                    mConsultProvider.getPatientsId()
                )
                callScreen.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "")
                callScreen.putExtra(
                    ConstantApp.ACTION_KEY_ENCRYPTION_MODE,
                    getResources().getStringArray(R.array.encryption_mode_values).get(0)
                )
                var receiverId: Long = 0L
                if (commonResponse.getProviderList().size() <= 2) {
                    for (i in 0 until commonResponse.getProviderList().size()) {
                        val pr: Provider = commonResponse.getProviderList().get(i)
                        if (pr.getId() != null && !pr.getId()
                                .equals(PrefUtility.getProviderId(this@ChatActivity))
                        ) {
                            receiverId = pr.getId()
                            break
                        }
                    }
                }
                if (receiverId != 0L) {
                    callScreen.putExtra("providerId", receiverId)
                }
                //                callScreen.putExtra("patientId", Long.parseLong(mConsultProvider.getPatientId()));
                callScreen.putExtra("callType", "outgoing")
                callScreen.putExtra("call", callType)
                //                Log.i(TAG, "call params " + commonResponse.getProviderList() + " " + receiverId);
                val gson: Gson = Gson()
                callScreen.putExtra("providerList", gson.toJson(commonResponse.getProviderList()))
                startActivity(callScreen)
            } else {
                val errMsg: String = ErrorMessages.getErrorMessage(
                    this@ChatActivity,
                    commonResponse.getErrorMessage(),
                    Constants.API.startCall
                )
                //                UtilityMethods.showErrorSnackBar(chatActBinding.rootLayout, errMsg, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    chatActBinding.rootLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
            }
        }
    }

    private fun startCall(callType: String) {
        if (membersMap!!.size < 2) {
            var message: String? = ""
            if (callType.equals(Constants.FCMMessageType.AUDIO_CALL, ignoreCase = true)) {
                message = getString(R.string.provider_invite_first_audio_call)
            } else if (callType.equals(Constants.FCMMessageType.VIDEO_CALL, ignoreCase = true)) {
                message = getString(R.string.provider_invite_first)
            }
            //            UtilityMethods.showErrorSnackBar(chatActBinding.getRoot(), message, Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                chatActBinding.getRoot(),
                this,
                CustomSnackBar.WARNING,
                message,
                CustomSnackBar.TOP,
                3000,
                0
            ).show()
            return
        }
        if (!UtilityMethods.isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            ).show()
            return
        }
        showProgressBar()
        val providerID: Long = PrefUtility.getProviderId(this@ChatActivity)
        val token: String =
            PrefUtility.getStringInPref(this@ChatActivity, Constants.SharedPrefConstants.TOKEN, "")
        //        chatViewModel.startCall(providerID, token, 0l, Long.valueOf(mConsultProvider.getPatientsId()), String.valueOf(mConsultProvider.getPatientsId()), callType).observe(ChatActivity.this, commonResponse -> {
        chatViewModel.startCall(
            providerID,
            token,
            0L,
            mConsultProvider.getPatientsId(),
            java.lang.String.valueOf(mConsultProvider.getPatientsId()),
            callType
        ).observe(this@ChatActivity) { commonResponse ->
            var openCallScreen: Boolean = true
            dismissProgressBar()
            if ((commonResponse != null) && (commonResponse.getStatus() != null) && commonResponse.getStatus()) {
            } else {
                val errMsg: String = ErrorMessages.getErrorMessage(
                    this@ChatActivity,
                    commonResponse.getErrorMessage(),
                    Constants.API.startCall
                )
                //                        Toast.makeText(ChatActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                if (membersMap != null && membersMap!!.size == 2) {
                    openCallScreen = false
                    //                    UtilityMethods.showErrorSnackBar(containerParent, errMsg, Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        containerParent,
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    ).show()
                }
            }
            if (openCallScreen) {
                val callScreen: Intent = Intent(this@ChatActivity, CallActivity::class.java)
                callScreen.putExtra(
                    ConstantApp.ACTION_KEY_CHANNEL_NAME,
                    java.lang.String.valueOf(mConsultProvider.getPatientsId())
                )
                callScreen.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "")
                callScreen.putExtra(
                    ConstantApp.ACTION_KEY_ENCRYPTION_MODE,
                    getResources().getStringArray(R.array.encryption_mode_values).get(0)
                )
                //                callScreen.putStringArrayListExtra("providerList", new ArrayList(membersMap.keySet()));
                callScreen.putExtra("patientId", mConsultProvider.getPatientsId())
                callScreen.putExtra("callType", "outgoing")
                callScreen.putExtra("call", callType)
                val gson: Gson = Gson()
                callScreen.putExtra("providerList", gson.toJson(commonResponse.getProviderList()))
                startActivity(callScreen)
            }
        }
        return
    }

    fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("messageValue", "messageReaded")
        setResult(RESULT_OK, intent)
        finish()
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else if (popup != null && popup.isShowing()) {
            popup.onDismiss()
        } else {
            super.onBackPressed()
        }
    }

    private fun handleSOSClick() {
        if (!UtilityMethods.isInternetConnected(this)) {
//            UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
            CustomSnackBar.make(
                binding.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.no_internet_connectivity),
                CustomSnackBar.TOP,
                3000,
                0
            ).show()
            return
        }
        val title: String =
            getString(R.string.sos_alert_confirmation_msg).toString() + " " + mConsultProvider.getName()
        val message: String = getString(R.string.alert_msg)
        customDialog = UtilityMethods.showDialog(this,
            title,
            message,
            false,
            R.string.send,
            View.OnClickListener {
                showProgressBar()
                val providerID: Long = PrefUtility.getProviderId(this@ChatActivity)
                val token: String = PrefUtility.getStringInPref(
                    this@ChatActivity,
                    Constants.SharedPrefConstants.TOKEN,
                    ""
                )
                //                chatViewModel.startSOS(providerID, token, Long.valueOf(mConsultProvider.getPatientsId()), "").observe(ChatActivity.this, commonResponse -> {
                chatViewModel.startSOS(providerID, token, mConsultProvider.getPatientsId(), "")
                    .observe(this@ChatActivity) { commonResponse ->
                        dismissProgressBar()
                        //                    if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus()) {
                        // Because of Custom Model class
                        if (commonResponse != null && commonResponse.getStatus()) {
                            val callScreen: Intent =
                                Intent(this@ChatActivity, CallActivity::class.java)
                            callScreen.putExtra(
                                "providerName",
                                mConsultProvider.getBdProviderName()
                            )
                            callScreen.putExtra(
                                "providerHospitalName",
                                mConsultProvider.getHospital()
                            )
                            callScreen.putExtra(
                                ConstantApp.ACTION_KEY_CHANNEL_NAME,
                                java.lang.String.valueOf(mConsultProvider.getPatientsId())
                            )
                            callScreen.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "")
                            callScreen.putExtra(
                                ConstantApp.ACTION_KEY_ENCRYPTION_MODE,
                                getResources().getStringArray(R.array.encryption_mode_values).get(0)
                            )
                            callScreen.putExtra("patientId", mConsultProvider.getPatientsId())
                            callScreen.putExtra("sos", true)
                            callScreen.putExtra("callType", "outgoing")
                            val gson: Gson = Gson()
                            callScreen.putExtra(
                                "providerList",
                                gson.toJson(commonResponse.getProviderList())
                            )
                            startActivity(callScreen)
                        } else {
                            val errMsg: String = ErrorMessages.getErrorMessage(
                                this@ChatActivity,
                                commonResponse.getErrorMessage(),
                                Constants.API.startCall
                            )
                            //                        Toast.makeText(ChatActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                            //                        UtilityMethods.showErrorSnackBar(containerParent, errMsg, Snackbar.LENGTH_LONG);
                            CustomSnackBar.make(
                                containerParent,
                                this@ChatActivity,
                                CustomSnackBar.WARNING,
                                errMsg,
                                CustomSnackBar.TOP,
                                3000,
                                0
                            ).show()
                        }
                    }
                customDialog.dismiss()
            },
            R.string.cancel,
            View.OnClickListener { customDialog.cancel() },
            Color.RED,
            true
        )
    }

    private fun inviteProvider() {
        showProgressBar("Inviting provider please wait.")
        val providerId: Long = PrefUtility.getProviderId(this@ChatActivity)
        val token: String =
            PrefUtility.getStringInPref(this@ChatActivity, Constants.SharedPrefConstants.TOKEN, "")
        chatViewModel.inviteProviderBroadCast(providerId, token, mConsultProvider.getId())
            .observe(this@ChatActivity,
                Observer<Any?> { commonResponse ->
                    dismissProgressBar()
                    //                Log.d(TAG, "Response Broadcast " + commonResponse.toString() + " " + mConsultProvider.getId() + " " + providerId);
                    if ((commonResponse != null) && (commonResponse.getStatus() != null) && commonResponse.getStatus()) {
                    } else {
                        val errMsg: String = ErrorMessages.getErrorMessage(
                            this@ChatActivity,
                            commonResponse.getErrorMessage(),
                            Constants.API.invite
                        )
                        Toast.makeText(this@ChatActivity, errMsg, Toast.LENGTH_SHORT).show()
                    }
                })
    }

    fun onClickDirectory() {
//        Log.i(TAG, "onClickDirectory: on click of directory");
        drawerLayout.openDrawer(GravityCompat.END)
        fetchDirectory()
    }

    fun fetchDirectory() {
//        Log.d(TAG, "fetchDirectory: called...");
        DirectoryListHelperOld(binding, viewModel, object : CallbackDirectory() {
            fun onClickProvierItem(provider: Provider) {
//                Log.i(TAG, "Hospital Name : " + provider.getHospital());
//                Log.i(TAG, "Provider Name : " + provider.getName());
                if (!UtilityMethods.isInternetConnected(this@ChatActivity)) {
//                    UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        binding.getRoot(),
                        this@ChatActivity,
                        CustomSnackBar.WARNING,
                        getString(R.string.no_internet_connectivity),
                        CustomSnackBar.TOP,
                        3000,
                        0
                    ).show()
                    return
                }
                val providerID: Long = PrefUtility.getProviderId(this@ChatActivity)
                val token: String = PrefUtility.getStringInPref(
                    this@ChatActivity,
                    Constants.SharedPrefConstants.TOKEN,
                    ""
                )
                chatViewModel.inviteProvider(
                    providerID,
                    token,
                    provider.getId(),
                    mConsultProvider.getId()
                ).observe(this@ChatActivity) { commonResponse ->
                    dismissProgressBar()
                    if ((commonResponse != null) && (commonResponse.getStatus() != null) && commonResponse.getStatus()) {
                    } else {
                        val errMsg: String = ErrorMessages.getErrorMessage(
                            this@ChatActivity,
                            commonResponse.getErrorMessage(),
                            Constants.API.invite
                        )
                        Toast.makeText(this@ChatActivity, errMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun updateMemberCount() {
        for (s: String in membersMap!!.keys) {
//            Log.d(TAG, s + " : " + membersMap.get(s));
            mFirebaseDatabaseReference!!.child("providers").child(s).child("active")
                .child((membersMap!![s])!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val consultProviderA: ConsultProvider? =
                            dataSnapshot.getValue(ConsultProvider::class.java)
                        //                            consultProviderA.setMemberCount(1+consultProviderA.getMemberCount());
                        mFirebaseDatabaseReference!!.child("providers").child(s).child("active")
                            .child(
                                (membersMap!![s])!!
                            ).child("memberCount").setValue(1 + consultProviderA.getMemberCount())
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    }

    private fun doDischargePatient() {

//        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        val dialog = Dialog(this, R.style.AppTheme_NoActionBarDark)
        dialog.setContentView(R.layout.activity_discharge_patient)
        dialog.show()
        val toolbarDischarge: Toolbar = dialog.findViewById(R.id.toolbarDischarge)
        toolbarDischarge.setTitle(getString(R.string.discharge_patient))
        toolbarDischarge.setNavigationIcon(R.drawable.ic_back)
        toolbarDischarge.setNavigationOnClickListener { dialog.dismiss() }
        val idContainerLayout = dialog.findViewById<LinearLayout>(R.id.idContainerLayout)
        val btnDischarge = dialog.findViewById<Button>(R.id.btnDischarge)
        val edtDischargeSummary = dialog.findViewById<EditText>(R.id.edtDischargeSummary)

/*        String role = PrefUtility.getRole(this);
        if (role.equalsIgnoreCase(Constants.ProviderRole.RD.toString())) {
            titleTxt.setText(getString(R.string.complete_consultation));
            completeButton.setText(R.string.complete);
        }

        String prefix = getString(R.string.assessment) + "\n\n" + getString(R.string.plan);
        edtDischargeSummary.setText(prefix);*/btnDischarge.setOnClickListener(View.OnClickListener { v ->
            val dischargeSummary = edtDischargeSummary.text.toString()
            if (TextUtils.isEmpty(dischargeSummary)) {
                //                    UtilityMethods.showErrorSnackBar(v, getString(R.string.summary_note_is_mandatory), Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    v,
                    this@ChatActivity,
                    CustomSnackBar.WARNING,
                    getString(R.string.summary_note_is_mandatory),
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
                return@OnClickListener
            }
            if (!UtilityMethods.isInternetConnected(this@ChatActivity)) {
                //                    UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    binding.getRoot(),
                    this@ChatActivity,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
                return@OnClickListener
            }
            dialog.dismiss()
            showProgressBar()
            val strPatientId: String = java.lang.String.valueOf(mConsultProvider.getPatientsId())
            val dischargePatientRequest = DischargePatientRequest()
            dischargePatientRequest.setPatientId(strPatientId)
            dischargePatientRequest.setDischargeSummary(dischargeSummary)
            chatViewModel.bspDischargePatient(dischargePatientRequest)
                .observe(this@ChatActivity) { commonResponse ->
                    //                    Log.d(TAG, "Discharge patient response: " + new Gson().toJson(commonResponse));
                    if ((commonResponse != null) && (commonResponse.getStatus() != null) && commonResponse.getStatus()) {
                        if (mConsultProvider != null) {
                            //                            mConsultProvider.setStatus(Constants.PatientStatus.Completed);
                            mConsultProvider.setStatus(Constants.PatientStatus.Discharged)
                            handleMenuItems()
                            onPatientDischargeSuccess(commonResponse)
                        }
                    } else {
                        btnDischarge.setEnabled(true)
                        val errMsg: String = ErrorMessages.getErrorMessage(
                            this@ChatActivity,
                            commonResponse.getErrorMessage(),
                            Constants.API.register
                        )
                        //                        UtilityMethods.showErrorSnackBar(idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                        CustomSnackBar.make(
                            idContainerLayout,
                            this@ChatActivity,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        ).show()
                    }
                }
        })
        dialog.show()
    }

    fun onPatientDischargeSuccess(commonResponse: omnicure.mvp.com.patientEndpoints.model.CommonResponse?) {
        CustomSnackBar.make(
            binding.getRoot(),
            this,
            CustomSnackBar.SUCCESS,
            getString(R.string.patient_discharged_successfully),
            CustomSnackBar.TOP,
            3000,
            1
        ).show()

//        final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this, R.style.CustomAlertDialog);
//        ViewGroup viewGroup = findViewById(android.R.id.content);
//        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_alert_dialog, viewGroup, false);
//        TextView alertTitle = dialogView.findViewById(R.id.alertTitle);
//        TextView alertMsg = dialogView.findViewById(R.id.alertMessage);
//        alertTitle.setText(getString(R.string.success));
//        alertTitle.setVisibility(View.GONE);
//        alertMsg.setText(getString(R.string.patient_discharged_successfully));
//        Button buttonOk = dialogView.findViewById(R.id.buttonOk);
//        builder.setView(dialogView);
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.setCanceledOnTouchOutside(false);
//        buttonOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
//            }
//        });
//        alertDialog.show();
    }

    private fun doCompleteConsultation() {

//        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        val dialog = Dialog(this, R.style.AppTheme_NoActionBarDark)
        dialog.setContentView(R.layout.mark_complete_dialog)
        dialog.show()
        val toolbarComplete: Toolbar = dialog.findViewById(R.id.toolbarComplete)
        toolbarComplete.setTitle(getString(R.string.complete_consultation))
        toolbarComplete.setNavigationIcon(R.drawable.ic_back)
        toolbarComplete.setNavigationOnClickListener { dialog.dismiss() }
        val idContainerLayout = dialog.findViewById<LinearLayout>(R.id.idContainerLayout)
        val btnComplete = dialog.findViewById<Button>(R.id.btnComplete)
        val edtAssessment = dialog.findViewById<EditText>(R.id.discharge_assessment)
        val edtPlan = dialog.findViewById<EditText>(R.id.discharge_plan)
        val prefixAssessment: String = getString(R.string.assessment)
        val prefixPlan: String = getString(R.string.plan)
        edtAssessment.setText(Html.fromHtml(prefixAssessment))
        edtPlan.setText(Html.fromHtml(prefixPlan))
        Selection.setSelection(edtAssessment.text, edtAssessment.text.length)
        Selection.setSelection(edtPlan.text, edtPlan.text.length)
        edtAssessment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith(Html.fromHtml(prefixAssessment).toString())) {
                    edtAssessment.setText(Html.fromHtml(prefixAssessment))
                    Selection.setSelection(edtAssessment.text, edtAssessment.text.length)
                }
            }
        })
        edtPlan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith(Html.fromHtml(prefixPlan).toString())) {
                    edtPlan.setText(Html.fromHtml(prefixPlan))
                    Selection.setSelection(edtPlan.text, edtPlan.text.length)
                }
            }
        })
        btnComplete.setOnClickListener(View.OnClickListener { v ->
            val assessment = edtAssessment.text.toString()
            val plan = edtPlan.text.toString()
            if (TextUtils.isEmpty(assessment) || assessment.equals(
                    "Assessment:",
                    ignoreCase = true
                )
            ) {
                if (TextUtils.isEmpty(plan) || plan.equals("Plan:", ignoreCase = true)) {
                    //                        UtilityMethods.showErrorSnackBar(v, getString(R.string.summary_note_is_mandatory), Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        v,
                        this@ChatActivity,
                        CustomSnackBar.WARNING,
                        getString(R.string.summary_note_is_mandatory),
                        CustomSnackBar.TOP,
                        3000,
                        0
                    ).show()
                    return@OnClickListener
                }
            }
            if (TextUtils.isEmpty(plan) || plan.equals("Plan:", ignoreCase = true)) {
                if (TextUtils.isEmpty(assessment) || assessment.equals(
                        "Assessment:",
                        ignoreCase = true
                    )
                ) {
                    //                        UtilityMethods.showErrorSnackBar(v, getString(R.string.summary_note_is_mandatory), Snackbar.LENGTH_LONG);
                    CustomSnackBar.make(
                        v,
                        this@ChatActivity,
                        CustomSnackBar.WARNING,
                        getString(R.string.summary_note_is_mandatory),
                        CustomSnackBar.TOP,
                        3000,
                        0
                    ).show()
                    return@OnClickListener
                }
            }
            if (!UtilityMethods.isInternetConnected(this@ChatActivity)) {
                //                    UtilityMethods.showInternetError(binding.getRoot(), Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    binding.getRoot(),
                    this@ChatActivity,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
                return@OnClickListener
            }
            dialog.dismiss()
            showProgressBar()
            val notes = "$assessment\n \n$plan"
            val providerID: Long = PrefUtility.getProviderId(this@ChatActivity)
            val token: String = PrefUtility.getStringInPref(
                this@ChatActivity,
                Constants.SharedPrefConstants.TOKEN,
                ""
            )
            chatViewModel.dischargePatient(providerID, token, mConsultProvider.getId(), notes)
                .observe(this@ChatActivity) { commonResponse ->
                    dismissProgressBar()
                    if ((commonResponse != null) && (commonResponse.getStatus() != null) && commonResponse.getStatus()) {
                        if (mConsultProvider != null) {
                            val providerName: String = PrefUtility.getStringInPref(
                                this@ChatActivity,
                                Constants.SharedPrefConstants.NAME,
                                ""
                            )
                            val role: String = PrefUtility.getStringInPref(
                                this@ChatActivity,
                                Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                                ""
                            )
                            mFirebaseDatabaseReference!!.child("providers")
                                .child(providerID.toString()).child("active")
                                .child(java.lang.String.valueOf(mConsultProvider.getId()))
                                .child("completed_by").setValue(providerName + ", " + role)
                            mConsultProvider.setStatus(Constants.PatientStatus.Completed)
                            handleMenuItems()
                            startActivity(
                                Intent(this@ChatActivity, HomeActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                            finish()
                        }
                    } else {
                        btnComplete.setEnabled(true)
                        val errMsg: String = ErrorMessages.getErrorMessage(
                            this@ChatActivity,
                            commonResponse.getErrorMessage(),
                            Constants.API.register
                        )
                        //                        UtilityMethods.showErrorSnackBar(idContainerLayout, errMsg, Snackbar.LENGTH_LONG);
                        CustomSnackBar.make(
                            idContainerLayout,
                            this@ChatActivity,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        ).show()
                    }
                }
        })
        dialog.show()
    }

    fun sendImage(uri: Uri?) {
//        Log.e("file uri parse ", uri.toString());
        try {
            val builder = AlertDialog.Builder(th, R.style.CustomAlertDialog)
            val viewGroup: ViewGroup = findViewById(android.R.id.content)
            val dialogView: View = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.alert_custom_dialog, viewGroup, false)
            val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
            val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
            alertTitle.setText(getString(R.string.uploadImage))
            alertMsg.setText(getString(R.string.alert_image_confirmation))
            val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
            val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
            builder.setView(dialogView)
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            buttonYes.setOnClickListener {
                showDownloadPB(
                    getString(R.string.image_uploading_pb_msg),
                    TaskType.upload.toString()
                )
                addAttachmentMessage(uri, "image")
                alertDialog.dismiss()
            }
            buttonNo.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
        } catch (e: Exception) {
//            Log.e("Exception:", e.toString());
        }
    }

    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                /*Uri uri = data.getExtras().getParcelable(Constants.ImageCaptureConstants.SCANNED_RESULT);
                defaultImageBitmap = getBitmapFromUri(uri);
                defaultImageBitmap = getResizedBitmap(defaultImageBitmap,500);
                addAttachmentMessage(uri, "image");*/
                try {
                    var selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor: Cursor = getContentResolver().query(
                            selectedImage,
                            filePathColumn, null, null, null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath = cursor.getString(columnIndex)
                            val angle = getRotateAngle(picturePath)
                            //                            Log.i(TAG, "Image angle " + angle);
//                            Log.i(TAG, "Picture Path : " + picturePath);
//                            Log.i(TAG, "Selected Image URI : " + selectedImage);
                            var bitmapFile: File? = File(picturePath)
                            val imageSize: Long = bitmapFile!!.length() / Constants.KB
                            //                            Log.i(TAG, "Origional Image Size " + imageSize + " kb");
                            if (imageSize > Constants.IMAGE_MAX_SIZE) {
                                bitmapFile = reduceImageBitmapFileSize(File(picturePath))
                                if (bitmapFile != null) {
//                                    Log.i(TAG, "Reduced Image Size " + bitmapFile.length() / Constants.KB + " kb");
                                    selectedImage = Uri.fromFile(bitmapFile)
                                    defaultImageBitmap =
                                        BitmapFactory.decodeFile(bitmapFile.absolutePath)
                                } else {
//                                    Log.d(TAG, "bitmapFile is null, could not reduce the image size");
                                    defaultImageBitmap = BitmapFactory.decodeFile(picturePath)
                                }
                            } else {
                                defaultImageBitmap = BitmapFactory.decodeFile(picturePath)
                                //defaultImageBitmap = getRotatedBitmap(picturePath,defaultImageBitmap);
                            }
                            val builder =
                                AlertDialog.Builder(this@ChatActivity, R.style.CustomAlertDialog)
                            val viewGroup: ViewGroup = findViewById(android.R.id.content)
                            val dialogView: View = LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.alert_custom_dialog, viewGroup, false)
                            val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
                            val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
                            alertTitle.setText(getString(R.string.uploadImage))
                            alertMsg.setText(getString(R.string.alert_image_confirmation))
                            val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
                            val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
                            builder.setView(dialogView)
                            val alertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.setCanceledOnTouchOutside(false)
                            val finalSelectedImage = selectedImage
                            buttonYes.setOnClickListener {
                                showDownloadPB(
                                    getString(R.string.image_uploading_pb_msg),
                                    TaskType.upload.toString()
                                )
                                addAttachmentMessage(finalSelectedImage, "image")
                                cursor.close()
                                alertDialog.dismiss()
                            }
                            buttonNo.setOnClickListener { alertDialog.dismiss() }
                            alertDialog.show()
                        }
                    }
                } catch (e: Exception) {
//                    Log.e(TAG, "Exception:", e.getCause());
                }
            }
        } else if (requestCode == REQUEST_FILE) {
            if (resultCode == RESULT_OK) {
                val builder = AlertDialog.Builder(this@ChatActivity, R.style.CustomAlertDialog)
                val viewGroup: ViewGroup = findViewById(android.R.id.content)
                val dialogView: View = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.alert_custom_dialog, viewGroup, false)
                val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
                val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
                alertTitle.setText(getString(R.string.uploadFile))
                alertMsg.setText(getString(R.string.alert_file_confirmation))
                val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
                val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
                builder.setView(dialogView)
                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                buttonYes.setOnClickListener {
                    showDownloadPB(
                        getString(R.string.sending_file_pb_msg),
                        TaskType.upload.toString()
                    )
                    addAttachmentMessage(data.data, "file")
                    alertDialog.dismiss()
                }
                buttonNo.setOnClickListener { alertDialog.dismiss() }
                alertDialog.show()
            }
        } else if (requestCode == REQUEST_VIDEO) {
            if (resultCode == RESULT_OK) {
                try {
                    val selectedVideo = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedVideo != null) {
                        val cursor: Cursor = getContentResolver().query(
                            selectedVideo,
                            filePathColumn, null, null, null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath = cursor.getString(columnIndex)
                            val bitmapFile = File(picturePath)
                            val fileSizeInBytes = bitmapFile.length()
                            val fileSizeInKB = fileSizeInBytes / 1024
                            val fileSizeInMB = fileSizeInKB / 1024
                            val fileName = bitmapFile.name
                            val extension = fileName.substring(fileName.lastIndexOf("."))
                            //                            Log.d(TAG, "extension : " + extension);
                            if (".mov".equals(extension, ignoreCase = true) || ".mp4".equals(
                                    extension,
                                    ignoreCase = true
                                )
                            ) {

//                                Log.i(TAG, "Selected Video bitmapFile : " + bitmapFile);
//                                Log.i(TAG, "Selected Video URI : " + selectedVideo);
//                                Log.i(TAG, "Original bitmapFile length : " + bitmapFile.length());
//                                Log.i(TAG, "Original Video Size : " + fileSizeInMB + " mb");

/*
                                if (fileSizeInMB > Constants.VIDEO_MAX_SIZE) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this, R.style.CustomAlertDialog);
                                    ViewGroup viewGroup = findViewById(android.R.id.content);
                                    View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_alert_dialog, viewGroup, false);
                                    TextView alertTitle = dialogView.findViewById(R.id.alertTitle);
                                    TextView alertMsg = dialogView.findViewById(R.id.alertMessage);
                                    alertTitle.setText(getString(R.string.alert));
                                    alertMsg.setText(getString(R.string.alert_video));
                                    Button buttonOk = dialogView.findViewById(R.id.buttonOk);
                                    builder.setView(dialogView);
                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.setCancelable(false);
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    buttonOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                    return;
                                }
*/
                                if (fileSizeInMB > Constants.VIDEO_MAX_SIZE) {
                                    CustomSnackBar.make(
                                        chatActBinding.getRoot(),
                                        this@ChatActivity,
                                        CustomSnackBar.WARNING,
                                        getString(R.string.alert_video),
                                        CustomSnackBar.TOP,
                                        3000,
                                        0
                                    ).show()
                                    return
                                }
                                val builder = AlertDialog.Builder(
                                    this@ChatActivity,
                                    R.style.CustomAlertDialog
                                )
                                val viewGroup: ViewGroup = findViewById(android.R.id.content)
                                val dialogView: View = LayoutInflater.from(getApplicationContext())
                                    .inflate(R.layout.alert_custom_dialog, viewGroup, false)
                                val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
                                val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
                                alertTitle.setText(getString(R.string.uploadVideo))
                                alertMsg.setText(getString(R.string.alert_video_confirmation))
                                val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
                                val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
                                builder.setView(dialogView)
                                val alertDialog = builder.create()
                                alertDialog.setCancelable(false)
                                alertDialog.setCanceledOnTouchOutside(false)
                                buttonYes.setOnClickListener {
                                    showDownloadPB(
                                        getString(R.string.sending_video_pb_msg),
                                        TaskType.upload.toString()
                                    )
                                    addAttachmentMessage(data.data, "video")
                                    alertDialog.dismiss()
                                }
                                buttonNo.setOnClickListener { alertDialog.dismiss() }
                                alertDialog.show()
                            } else {
                                CustomSnackBar.make(
                                    chatActBinding.getRoot(),
                                    this@ChatActivity,
                                    CustomSnackBar.WARNING,
                                    "File not supported",
                                    CustomSnackBar.TOP,
                                    3000,
                                    0
                                ).show()
                            }
                        }
                    }
                } catch (e: Exception) {
//                    Log.e(TAG, "Exception:", e.getCause());
                }
            }
        } else if (requestCode == REQUEST_AUDIO) {
            if (resultCode == RESULT_OK) {
                addAttachmentMessage(data.data, "audio")
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                val builder = AlertDialog.Builder(this@ChatActivity, R.style.CustomAlertDialog)
                val viewGroup: ViewGroup = findViewById(android.R.id.content)
                val dialogView: View = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.alert_custom_dialog, viewGroup, false)
                val alertTitle = dialogView.findViewById<TextView>(R.id.alertTitle)
                val alertMsg = dialogView.findViewById<TextView>(R.id.alertMessage)
                alertTitle.setText(getString(R.string.uploadImage))
                alertMsg.setText(getString(R.string.alert_image_confirmation))
                val buttonYes = dialogView.findViewById<TextView>(R.id.buttonYes)
                val buttonNo = dialogView.findViewById<TextView>(R.id.buttonNo)
                builder.setView(dialogView)
                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                buttonYes.setOnClickListener {
                    showDownloadPB(
                        getString(R.string.image_uploading_pb_msg),
                        TaskType.upload.toString()
                    )
                    addCameraMessage(data, "image")
                    alertDialog.dismiss()
                }
                buttonNo.setOnClickListener { alertDialog.dismiss() }
                alertDialog.show()
            }
        }
    }

    fun reduceImageBitmapFileSize(file: File?): File? {
        var file = file
        try {

            // BitmapFactory options to downsize the image
            val imageSize: Long = file!!.length() / Constants.KB
            val sampling = imageSize.toInt() / 100
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            //            Log.i(TAG, "Scale : " + scale);
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            //file.createNewFile();
            file = createImageFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            return file
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
            return null
        }
    }

    private fun createImageFile(): File {
        clearTempImages()
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File(
            Constants.ImageCaptureConstants.IMAGE_PATH, ("IMG_" + timeStamp +
                    ".jpg")
        )
    }

    private fun clearTempImages(): Boolean {
        try {
            val tempFolder: File = File(Constants.ImageCaptureConstants.IMAGE_PATH)
            /*for (File f : tempFolder.listFiles())
            {
                boolean b=  f.delete();
                return b ;
            }*/
            val files = tempFolder.listFiles()
            if (files != null && files[0] != null) {
                return files[0]!!.delete()
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return false
    }

    private fun addAttachmentMessage(uri: Uri?, type: String) {
        if (uri != null) {
            //final Uri uri = data.getData();
            try {
                uploadInProgress = true
                uploadType = type
                var fname: String? = null
                if (uri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = getContentResolver().query(uri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            fname =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            //                            Log.d(TAG, "fname: " + fname);
                        }
                    } catch (e: Exception) {
//                        Log.e(TAG, "Exception:", e.getCause());
                    }
                }
                if (fname == null) {
                    val arr = URLDecoder.decode(uri.toString(), "UTF-8").split("/").toTypedArray()
                    fname = arr[arr.size - 1]
                }
                val filename = fname
                //                Log.d(TAG, "Uri: " + uri.toString());
//                Log.d(TAG, "fileType : " + type);
//                Log.d(TAG, "filename: " + filename);
//                Log.d(TAG, "uri last segment: " + uri.getLastPathSegment());
                mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!).push()
                    .setValue(null, object : DatabaseReference.CompletionListener {
                        var error = ""
                        override fun onComplete(
                            databaseError: DatabaseError?,
                            databaseReference: DatabaseReference
                        ) {
                            if (databaseError == null) {
                                val key = databaseReference.key
                                //                                    System.out.println("database key " + key);
                                val storageReference = FirebaseStorage.getInstance()
                                    .getReference(mFirebaseUser!!.uid)
                                    .child((key)!!)
                                    .child((uri.lastPathSegment)!!)


/*
                                    if ("video".equalsIgnoreCase(type)) {
                                        putVideoThumbInStorage(storageReference, uri, key, filename, type);
                                    }
*/putFileInStorage(storageReference, uri, key, filename, type)
                            } else {
                                error = getString(R.string.upload_failed_error)
                                handleFileUploadError(error)
                                //                                    Log.w(TAG, "Unable to write message to database.",
//                                            databaseError.toException());
                            }
                        }
                    })
            } catch (e: Exception) {
//                Log.e(TAG, "Exception:", e.getCause());
                val error: String = getString(R.string.upload_failed_error)
                handleFileUploadError(error)
            }
        }
    }

    fun getRealPathFromURI(contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun cancelUploadingTask() {
        try {
            if (mFileUploadTask != null && mMessageKey != null) {
//                System.out.println("coming here11 " + mFileUploadTask + " " + mMessageKey);
                mFileUploadTask!!.addOnCanceledListener(object : OnCanceledListener {
                    override fun onCanceled() {
//                        Log.d(TAG, "onCanceled: mFileUploadTask");
                        try {
                            mFileUploadTask!!.removeOnCanceledListener(this)
                            //Remove message from firebase
                            mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!)
                                .child(mMessageKey!!).removeValue()
                            uploadInProgress = false
                            mFileUploadTask = null
                        } catch (e: Exception) {
//                            Log.e(TAG, "Exception:", e.getCause());
                        }
                    }
                })
                mFileUploadTask!!.cancel()
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    private fun cancelFileDownloadingTask() {
        try {
            if (mFileDownloadTask != null && mFileDownloadTask!!.status == AsyncTask.Status.RUNNING) {
                mFileDownloadTask!!.cancel(true)
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    private fun onHandOffSendSuccess(response: omnicure.mvp.com.patientEndpoints.model.CommonResponse) {
//        UtilityMethods.showMessageSnackBar(idContainerLayout, getString(R.string.Handoff_patient_successfully));
        mHandler.postDelayed({
            setResult(RESULT_OK)
            finish()
        }, Constants.SNACKBAR_LENGTH_MEDIUM)
    }

    private fun putFileInStorage(
        storageReference: StorageReference,
        uri: Uri,
        key: String?,
        filename: String?,
        type: String
    ) {
        // Create file metadata including the content type
        val extention = getExtension(filename)
        //        Log.d(TAG, "putFileInStorage: extension " + extention);
        val metadata = StorageMetadata.Builder()
            .setContentType(extention)
            .build()
        //        Log.d(TAG, "putFileInStorage metadata : " + metadata.getContentType());
        /*mFileUploadTask = storageReference
                .child("/Videos")
                .putFile(uri, metadata);*/mFileUploadTask = storageReference.putFile(uri, metadata)
        mMessageKey = key
        val progressListner: OnProgressListener<UploadTask.TaskSnapshot> =
            OnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                //                Log.d(TAG, "putFileInStorage onProgress : " + progress + ", transferred: " + taskSnapshot.getBytesTransferred() + ", total: " + taskSnapshot.getTotalByteCount());
                if (downloadPB != null && downloadPB.isShowing()) {
                    downloadPB.setProgress(progress.toInt())
                }
            }
        mFileUploadTask!!.addOnProgressListener(progressListner)
        mFileUploadTask.addOnCompleteListener(this@ChatActivity,
            object : OnCompleteListener<UploadTask.TaskSnapshot> {
                var error = ""
                override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                    try {
                        if (mFileUploadTask != null) {
                            mFileUploadTask!!.removeOnCompleteListener(this)
                        }

//                            Log.d(TAG, "putFileInStorage addOnCompleteListener response : " + task.isSuccessful());
//                            Log.d(TAG, "putFileInStorage addOnCompleteListener getResult : " + task.getResult().toString());
                        if (task.isSuccessful) {
                            task.result!!.metadata!!.reference!!.downloadUrl
                                .addOnCompleteListener(this@ChatActivity,
                                    OnCompleteListener<Uri> { task ->
                                        //                                                        Log.d(TAG, "onComplete :" + task.isSuccessful());
                                        if (task.isSuccessful) {
                                            //                                                            Log.d(TAG, "generated file upload response :" + task.getResult().toString());
                                            val url: String =
                                                AESUtils.encryptData(task.result.toString(), encKey)
                                            val consultMessage = ConsultMessage(
                                                null,
                                                mUsername,
                                                mPhotoUrl,
                                                url,
                                                type,
                                                getTime(),
                                                senderId,
                                                filename,
                                                "Sent",
                                                role,
                                                0L
                                            )
                                            consultMessage.setId(key)
                                            mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!)
                                                .child((key)!!)
                                                .setValue(consultMessage)
                                            if ("video".equals(type, ignoreCase = true)) {
                                                putVideoThumbInStorage(
                                                    uri,
                                                    key,
                                                    filename,
                                                    type,
                                                    consultMessage
                                                )
                                            }
                                            dismissDownloadPB()
                                            val asyncTask: AsyncTaskUpdateUnread =
                                                AsyncTaskUpdateUnread()
                                            asyncTask.execute(consultMessage)
                                        }
                                    })
                        } else {
                            //if no internet and try to upload image we will get error here
                            if (task.isCanceled) {
                                handleFileUploadError("")
                            } else {
                                error = getString(R.string.upload_failed_error)
                            }

//                                Log.w(TAG, "File upload task was not successful ", task.getException());
                        }
                        if (mFileUploadTask != null) {
                            mFileUploadTask!!.removeOnProgressListener(progressListner)
                        }
                    } catch (e: Exception) {
//                            Log.e(TAG, "Exception:", e.getCause());
                        error = getString(R.string.upload_failed_error)
                    }
                    if (!TextUtils.isEmpty(error)) {
                        handleFileUploadError(error)
                    }
                }
            })
    }

    private fun putVideoThumbInStorage(
        uri: Uri,
        key: String?,
        filename: String?,
        type: String,
        consultMessage: ConsultMessage
    ) {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(this, uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//            Bitmap bitmap = mmr.getScaledFrameAtTime(1000, MediaMetadataRetriever.OPTION_NEXT_SYNC, 128, 128);
            val bitmap = mmr.getFrameAtTime(1000)
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val dataArr = baos.toByteArray()
            //            Log.d(TAG, "addAttachmentMessage: " + dataArr);
            val storageReference = FirebaseStorage.getInstance()
                .getReference(mFirebaseUser!!.uid)
                .child((key)!!)
                .child(Date().time.toString())
            storageReference.putBytes(dataArr).addOnCompleteListener(this@ChatActivity,
                OnCompleteListener<UploadTask.TaskSnapshot> { task ->
                    if (task.isSuccessful) {
                        task.result!!.metadata!!.reference!!.downloadUrl
                            .addOnCompleteListener(this@ChatActivity,
                                OnCompleteListener<Uri> { task ->
                                    if (task.isSuccessful) {
                                        consultMessage.setId(key)
                                        //                                                            Log.i(TAG, "onCompleteurl : " + task.getResult().toString());
                                        val url: String =
                                            AESUtils.encryptData(task.result.toString(), encKey)
                                        consultMessage.setThumbUrl(url)
                                        mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!)
                                            .child(
                                                (key)
                                            ).child("thumbUrl")
                                            .setValue(url)
                                        //                                                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                        //                                                                    .setValue(consultMessage);
                                        //                                                            AsyncTaskUpdateUnread asyncTask = new AsyncTaskUpdateUnread();
                                        //                                                            asyncTask.execute(consultMessage);
                                    }
                                })
                    } else {
                        //                                Log.w(TAG, "File upload task was not successful.",
                        //                                        task.getException());
                    }
                })
        }
        mmr.close()
    }

    private fun addCameraMessage(data: Intent?, type: String) {
        if (data != null) {
            try {
                val uri =
                    data.extras!!.getParcelable<Uri>(Constants.ImageCaptureConstants.SCANNED_RESULT)
                //                Log.d(TAG, "addCameraMessage: " + uri);
                var bitmap: Bitmap?
                var imageFile: File? = File(uri!!.path)
                //                Log.i(TAG, "addCameraMessage: " + imageFile.getAbsolutePath());
                val imageSize: Int = imageFile!!.length().toInt() / Constants.KB
                //                Log.i(TAG, "Camera Image Origional Size : " + imageSize + " kb");
                if (imageSize > Constants.IMAGE_MAX_SIZE) {
                    val angle = getRotateAngle(uri.path)
                    imageFile = reduceImageBitmapFileSize(imageFile)
                    bitmap = BitmapFactory.decodeFile(imageFile!!.path)
                    bitmap = rotateImage(bitmap, angle.toFloat())
                    //                    Log.i(TAG, "Camera Image Reduced Size : " + imageFile.length() / Constants.KB + " kb");
                } else {
                    bitmap = getBitmapFromUri(uri)
                    bitmap = getRotatedBitmap(uri.path, bitmap)
                }
                defaultImageBitmap = bitmap
                uploadInProgress = true
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val dataArr = baos.toByteArray()
                val filename = "image"

//                Log.d(TAG, "filename: " + filename);
                val tempMessage = ConsultMessage(
                    null, mUsername, mPhotoUrl,
                    null, type, getTime(), senderId, filename, "Sent", role, 0L
                )
                mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!).push()
                    .setValue(
                        null
                    ) { databaseError, databaseReference ->
                        if (databaseError == null) {
                            val key = databaseReference.key
                            val storageReference = FirebaseStorage.getInstance()
                                .getReference(mFirebaseUser!!.uid)
                                .child((key)!!)
                                .child(getTime().toString() + "")
                            putCameraFileInStorage(storageReference, dataArr, key, filename, type)
                        } else {
                            val error: String = getString(R.string.upload_failed_error)
                            handleFileUploadError(error)
                            //                                    Log.w(TAG, "Unable to write message to database.",
                            //                                            databaseError.toException());
                        }
                    }
            } catch (e: Exception) {
                val error: String = getString(R.string.upload_failed_error)
                handleFileUploadError(error)
                //                Log.e(TAG, "Exception:", e.getCause());
            }
        }
    }

    private fun putCameraFileInStorage(
        storageReference: StorageReference,
        dataArr: ByteArray,
        key: String?,
        filename: String,
        type: String
    ) {
        mFileUploadTask = storageReference.putBytes(dataArr)
        mMessageKey = key
        val progressListner: OnProgressListener<UploadTask.TaskSnapshot> =
            OnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                //                Log.d(TAG, "onProgress: " + progress + ", transferred: " + taskSnapshot.getBytesTransferred() + ", total: " + taskSnapshot.getTotalByteCount());
                if (downloadPB != null && downloadPB.isShowing()) {
                    downloadPB.setProgress(progress.toInt())
                }
            }
        mFileUploadTask!!.addOnProgressListener(progressListner)
        mFileUploadTask.addOnCompleteListener(this@ChatActivity,
            object : OnCompleteListener<UploadTask.TaskSnapshot> {
                var error = ""
                override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                    try {
                        if (mFileUploadTask != null) {
                            mFileUploadTask!!.removeOnCompleteListener(this)
                        }
                        if (task.isSuccessful) {
                            task.result!!.metadata!!.reference!!.downloadUrl
                                .addOnCompleteListener(this@ChatActivity,
                                    OnCompleteListener<Uri> { task ->
                                        if (task.isSuccessful) {
                                            val url: String =
                                                AESUtils().encryptData(task.result.toString(), encKey)
                                            val consultMessage = ConsultMessage(
                                                null,
                                                mUsername,
                                                mPhotoUrl,
                                                url,
                                                type,
                                                getTime(),
                                                senderId,
                                                filename,
                                                "Sent",
                                                role,
                                                0L
                                            )
                                            consultMessage.setId(key)
                                            mFirebaseDatabaseReference!!.child((MESSAGES_CHILD)!!)
                                                .child((key)!!)
                                                .setValue(consultMessage)
                                            dismissDownloadPB()
                                            val asyncTask: AsyncTaskUpdateUnread =
                                                AsyncTaskUpdateUnread()
                                            asyncTask.execute(consultMessage)
                                        }
                                    })
                        } else {
//                                Log.w(TAG, "File upload task was not successful.",
//                                        task.getException());
                            if (task.isCanceled) {
                                handleFileUploadError("")
                            } else {
                                error = getString(R.string.upload_failed_error)
                            }
                        }
                        if (mFileUploadTask != null) {
                            mFileUploadTask!!.removeOnProgressListener(progressListner)
                        }
                    } catch (e: Exception) {
//                            Log.e(TAG, "Exception:", e.getCause());
                        error = getString(R.string.upload_failed_error)
                    }
                    if (!TextUtils.isEmpty(error)) {
                        handleFileUploadError(error)
                    }
                }
            })
    }

    private fun setToolbar() {
        var nameStr: String = mConsultProvider.getName()
        var ageGenderStr: String = ""
        if (mConsultProvider.getDob() !== -1) {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            calendar.timeInMillis = mConsultProvider.getDob()
            val age = year - calendar[Calendar.YEAR]
            ageGenderStr =
                " " + getString(R.string.bullet_symbol).toString() + " " + age.toString() + ""
        }

/*        if (mConsultProvider.getUrgent() != null && mConsultProvider.getUrgent()) {
//            chatActBinding.idUrgentIcon.setVisibility(View.VISIBLE);
            chatActBinding.idUrgentIcon.setVisibility(View.GONE);
        }*/if ("Female".equals(mConsultProvider.getGender(), ignoreCase = true)) {
            ageGenderStr += " F"
        } else {
            ageGenderStr += " M"
        }
        //        setSupportActionBar(chatActBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false)
        }
        chatActBinding.imgBack.setOnClickListener(View.OnClickListener { finish() })


//        if (nameStr.trim().length() > 15) {
//            nameStr = nameStr.substring(0, 15) + "...";
//        }
        //Changed from 15 char to 10 to avoid name,gender,age colliding in title header
        if (nameStr.trim { it <= ' ' }.length > 10) {
            nameStr = nameStr.substring(0, 10) + "..."
        }
        chatActBinding.idToolbarTitle.setText(nameStr)
        chatActBinding.idPatientAgeGender.setText(ageGenderStr)
        chatActBinding.toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_menu_overflow))
        chatActBinding.toolbar.setNavigationOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.putExtra("messageValue", "messageReaded")
            setResult(RESULT_OK, intent)
            finish()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun handleChatMessage(
        consultMessage: ConsultMessage,
        binding: ItemMessageBinding, position: Int, dbReference: DatabaseReference
    ) {
        try {
            var isLeft = true
            //            Log.d(TAG, "handleChatMessage: " + position + " " + new Gson().toJson(consultMessage));

            // Hashmap list used for moving the chat to specific position from urgent message activity
            moveToChatHashMap[position] = consultMessage
            if (senderId.equals(consultMessage.getSenderId(), ignoreCase = true)) {
                isLeft = false
            }
            //            Log.d(TAG, "consultMessage Right: " + new Gson().toJson(consultMessage));
            binding.systemTextView.setVisibility(View.GONE)
            binding.tvTeamName.setVisibility(View.GONE)
            binding.leftViewLayout.setVisibility(View.GONE)
            binding.messageViewLeft.setVisibility(TextView.GONE)
            binding.messageTextViewLeft.setVisibility(View.GONE)
            binding.imageViewLeftLayout.setVisibility(View.GONE)
            binding.fileViewLeft.setVisibility(View.GONE)
            binding.statusTextViewLeft.setVisibility(View.GONE)
            binding.layoutTextRight.setVisibility(View.GONE)
            binding.messageViewRight.setVisibility(TextView.GONE)
            binding.messageTextViewRight.setVisibility(View.GONE)
            binding.imageViewRightLayout.setVisibility(View.GONE)
            binding.fileViewRight.setVisibility(View.GONE)
            binding.statusTextViewRight.setVisibility(View.VISIBLE)
            binding.messageViewLeft.setBackground(getResources().getDrawable(R.drawable.chat_bubble_left_updated))
            binding.messageTextViewLeft.setTextColor(getResources().getColor(R.color.white))
            binding.messengerTextViewLeft.setTextColor(getResources().getColor(R.color.white))
            binding.timeTextViewLeft.setTextColor(getResources().getColor(R.color.chat_time_color))
            binding.messageViewRight.setBackground(getResources().getDrawable(R.drawable.chat_bubble_right_updated))
            if (!isIncludeChat(patientHistory, consultMessage)) {
                return
            }

            /*float alpha = isShowingSelectedChatHisotry ? 0.85f : 1f;
            binding.systemTextView.setAlpha(alpha);
            binding.leftViewLayout.setAlpha(alpha);
            binding.imageViewLeftLayout.setAlpha(alpha);
            binding.fileViewLeft.setAlpha(alpha);

            binding.layoutTextRight.setAlpha(alpha);
            binding.imageViewRightLayout.setAlpha(alpha);
            binding.fileViewRight.setAlpha(alpha);*/

            // show play icon only to video thumbnail
            if (MessageType.video.toString().equals(consultMessage.getType(), ignoreCase = true)) {
                binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                binding.imgPlayIconRight.setVisibility(View.VISIBLE)
            } else {
                binding.imgPlayIconLeft.setVisibility(View.GONE)
                binding.imgPlayIconRight.setVisibility(View.GONE)
            }
            val isImage = consultMessage.getType().equalsIgnoreCase(MessageType.image.toString()) ||
                    consultMessage.getType().equalsIgnoreCase(MessageType.video.toString())
            if (uploadInProgress && isImage && (mFirebaseAdapter!!.getItemCount() - 1 == position)) {
                Glide.with(binding.messageImageViewRight.getContext())
                    .load(defaultImageBitmap)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            defaultImageDrawable = resource
                            return false
                        }
                    }).into<Target<Drawable>>(binding.messageImageViewRight)
                binding.messageImageViewRight.setTag("imageUpload")
                binding.imageViewRightLayout.setVisibility(View.VISIBLE)
            }
            //            Log.d(TAG, "ChatConsultMessage : " + position + " " + new Gson().toJson(consultMessage));
            binding.imageViewLeftLayout.setOnClickListener(View.OnClickListener {
                val imageUrl: String = AESUtils.decryptData(consultMessage.getImageUrl(), encKey)
                if (MessageType.image.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    val intent = Intent(this@ChatActivity, WebviewActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, imageUrl)
                    startActivity(intent)
                }
                if (MessageType.video.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    val intent = Intent(this@ChatActivity, VideoPlayerActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, imageUrl)
                    startActivity(intent)
                }
            })
            binding.imageViewRightLayout.setOnClickListener(View.OnClickListener {
                val imageUrl: String = AESUtils.decryptData(consultMessage.getImageUrl(), encKey)
                if (MessageType.image.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    val intent = Intent(this@ChatActivity, WebviewActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, imageUrl)
                    startActivity(intent)
                }
                if (MessageType.video.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    val intent = Intent(this@ChatActivity, VideoPlayerActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, imageUrl)
                    startActivity(intent)
                }
            })
            binding.messageViewLeft.setOnClickListener(View.OnClickListener {
                val imageUrl: String = AESUtils.decryptData(consultMessage.getImageUrl(), encKey)
                if (MessageType.file.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    val intent = Intent(this@ChatActivity, PDFViewerActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.PDF_URL, imageUrl)
                    startActivity(intent)
                }
            })
            binding.messageViewRight.setOnClickListener(View.OnClickListener {
                val imageUrl: String = AESUtils.decryptData(consultMessage.getImageUrl(), encKey)
                if (MessageType.file.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    val intent = Intent(this@ChatActivity, PDFViewerActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.PDF_URL, imageUrl)
                    startActivity(intent)
                }
            })

            // Sender side long click listener for mark/unmark as urgent for normal
            binding.messageViewRight.setOnLongClickListener { v ->
//                Toast.makeText(ChatActivity.this, "Long click", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "handleChatMessage: rightNormalMsgClickLong-->" + new Gson().toJson(consultMessage));
//                Log.e(TAG, "handleChatMessage: rightNormalMsgClickLong position-->" + position);

                // Open pop up only if the type is one of the type's below
                if ((consultMessage.getType()
                        .equalsIgnoreCase(MessageType.text.toString()) || consultMessage.getType()
                        .equalsIgnoreCase(MessageType.video.toString()) ||
                            consultMessage.getType()
                                .equalsIgnoreCase(MessageType.image.toString()) || consultMessage.getType()
                        .equalsIgnoreCase(MessageType.file.toString()))
                ) {
//                    Log.e(TAG, "handleChatMessage:messageViewRight Valid type");
                    // Long press vibration
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    var lastItem: Boolean = false
                    lastItem = position == (mFirebaseAdapter!!.getItemCount() - 1)
                    val y: Float =
                        mMessageRecyclerView!!.getY() + mMessageRecyclerView!!.getChildAt(position)
                            .getY()
                    //                Log.e(TAG, "messageViewRight: yValue-->"+ (int)y );
//                Log.e(TAG, "messageViewRight: yValue -100-->"+ (((int)y) - 100) );

                    // Hide keyboard to avoid UI misalignment while showing mark as urgent pop-up
                    if (isIsKeyboardShowing) {
                        hideSoftKeyboard()
                    }

                    // For mark/unmark popup Urgent UI alignment issues to scroll
                    if (y.toInt() > 1000) {
//                    chatActBinding.scrollView.smoothScrollTo(0, (((int)y) - 500) );
                        chatActBinding.scrollView.smoothScrollTo(0, ((y.toInt()) - 500))
                    }

                    // Popup UI for mark/unmark as urgent
                    showMarkUnMarkAsUrgentPopup(
                        this@ChatActivity,
                        v,
                        consultMessage,
                        binding,
                        lastItem
                    )
                }
                false
            }

            // Sender side long click listener for mark/unmark as urgent for attachment
            binding.imageViewRightLayout.setOnLongClickListener { v ->
//                Toast.makeText(ChatActivity.this, "Long click", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "handleChatMessage: rightAttachFileClickLong-->" + new Gson().toJson(consultMessage));

                // Open pop up only if the type is one of the type's below
                if ((consultMessage.getType()
                        .equalsIgnoreCase(MessageType.text.toString()) || consultMessage.getType()
                        .equalsIgnoreCase(MessageType.video.toString()) ||
                            consultMessage.getType()
                                .equalsIgnoreCase(MessageType.image.toString()) || consultMessage.getType()
                        .equalsIgnoreCase(MessageType.file.toString()))
                ) {
//                    Log.e(TAG, "handleChatMessage:imageViewRightLayout Valid type");
                    // Long press vibration
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    var lastItem: Boolean = false
                    lastItem = position == (mFirebaseAdapter!!.getItemCount() - 1)
                    val y: Float =
                        mMessageRecyclerView!!.getY() + mMessageRecyclerView!!.getChildAt(position)
                            .getY()
                    //                Log.e(TAG, "imageViewRightLayout: yValue -100-->"+ (((int)y) - 100) );
//                Log.e(TAG, "imageViewRightLayout: yValue-->"+ (int)y);

                    // Hide keyboard to avoid UI misalignment while showing mark as urgent pop-up
                    if (isIsKeyboardShowing) {
                        hideSoftKeyboard()
                    }
                    val displayMetrics: DisplayMetrics = DisplayMetrics()
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
                    val screenHeight: Int = displayMetrics.heightPixels

//                    Log.e(TAG, "handleChatMessage:imageViewRightLayout Valid type " + mMessageRecyclerView.getChildAt(position).getHeight() + " " + screenHeight + " " + mMessageRecyclerView.getChildAt(position).getY() + " " + mMessageRecyclerView.getY());
                    // For mark/unmark popup Urgent UI alignment issues
                    val remainSpaceBottom: Float =
                        (mMessageRecyclerView!!.getChildAt(position)
                            .getY() + mMessageRecyclerView!!.getChildAt(position)
                            .getHeight()) - screenHeight
                    //                    Log.i(TAG, "handleChatMessage: remainSpaceBottom " + remainSpaceBottom);
                    if (y.toInt() > 1000) {
                        chatActBinding.scrollView.smoothScrollTo(0, ((y.toInt()) - 100))
                    }
                    if (remainSpaceBottom < 0) {
                        chatActBinding.scrollView.smoothScrollTo(0, ((y.toInt()) - 100))
                    }

                    // Popup UI for mark/unmark as urgent
                    showMarkUnMarkAsUrgentPopup(
                        this@ChatActivity,
                        v,
                        consultMessage,
                        binding,
                        lastItem
                    )
                }
                false
            }
            if (MessageType.system.toString().equals(
                    consultMessage.getType(),
                    ignoreCase = true
                ) && consultMessage.getText() != null
            ) {
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                if (position == 0) {
                    val leftRightMargin: Int = UtilityMethods.dpToPx(15)
                    params.setMargins(leftRightMargin, 0, leftRightMargin, 0)
                    params.gravity = Gravity.CENTER
                    binding.systemTextView.setLayoutParams(params)
                } else {
                    val leftRightMargin: Int = UtilityMethods.dpToPx(15)
                    val topBottomMargin: Int = UtilityMethods.dpToPx(8)
                    params.gravity = Gravity.CENTER
                    params.setMargins(leftRightMargin, topBottomMargin, leftRightMargin, 0)
                    binding.systemTextView.setLayoutParams(params)
                }
                val systemMessage: String =
                    AESUtils.decryptData(consultMessage.getText().trim(), encKey)

//                Log.d(TAG, "consultMessage : " + consultMessage.getStatus());
//                Log.d(TAG, "consultMessage in string: " + position + " " + new Gson().toJson(consultMessage));
                binding.systemTextView.setVisibility(View.VISIBLE)
                binding.systemTextView.setText(
                    systemMessage + ", " + ChatUtils.getStatusDateFormat(
                        consultMessage.getTime()
                    )
                )
                if (systemMessage.contains("has been created")) {
                    binding.tvTeamName.setVisibility(View.VISIBLE)
                    val strReference = "has been created"
                    val strMessage = systemMessage
                    val strTeamName = strMessage.substring(
                        strMessage.indexOf(" and ") + 5,
                        strMessage.indexOf(strReference)
                    )
                    binding.tvTeamName.setText(strTeamName.trim { it <= ' ' })
                    if (teamButtonEnabled) {
                        teamButtonView!!.isEnabled = false
                    }
                    teamButtonView = binding.tvTeamName
                    teamButtonEnabled = true

//                    Log.d(TAG, "Updated TeamName : " + strConsultTeamName + " : " + strTeamName.trim());
                    Log.d(TAG, "patient status__ " + mConsultProvider.getStatus())
                    binding.tvTeamName.setEnabled(false)
                    if (mConsultProvider.getStatus() === Constants.PatientStatus.Discharged ||
                        mConsultProvider.getStatus() === Constants.PatientStatus.Completed
                    ) {
                        binding.tvTeamName.setEnabled(false)
                        chatActBinding.imgTeamCall.setVisibility(View.INVISIBLE)
                        chatActBinding.linearLayout.setVisibility(View.GONE)
                    } else if (mConsultProvider.getStatus()
                            .equals(Constants.PatientStatus.Handoff)
                    ) {
                        binding.tvTeamName.setEnabled(false)
                        chatActBinding.imgTeamCall.setEnabled(false)
                    } else if ((mConsultProvider.getStatus().equals(Constants.PatientStatus.Invited)
                                || mConsultProvider.getStatus()
                            .equals(Constants.PatientStatus.Pending))
                    ) {
                        chatActBinding.imgTeamCall.setVisibility(View.INVISIBLE)
                    } else if (chatActBinding.acceptBtn.getVisibility() === View.VISIBLE) {
                        binding.tvTeamName.setEnabled(false)
                        chatActBinding.imgTeamCall.setEnabled(false)
                    } else if ((!TextUtils.isEmpty(strConsultTeamName)) && strConsultTeamName.equals(
                            strTeamName.trim { it <= ' ' },
                            ignoreCase = true
                        )
                    ) {
                        binding.tvTeamName.setEnabled(true)
                        chatActBinding.imgTeamCall.setEnabled(true)
                    } else {
//                        System.out.println("coming here__ ");
                        binding.tvTeamName.setEnabled(false)
                        chatActBinding.imgTeamCall.setEnabled(false)
                    }
                    binding.tvTeamName.setOnClickListener(View.OnClickListener {
                        handleMultipleClicks(binding.tvTeamName)
                        /*        long patientId = !TextUtils.isEmpty(mConsultProvider.getPatientId()) ? Long.parseLong(mConsultProvider.getPatientId()) : 0;
                                            Intent intentTeamChat = new Intent(ChatActivity.this, TeamGroupChatActivity.class);
                                            intentTeamChat.putExtra("patientID", patientId);
                                            intentTeamChat.putExtra(Constants.IntentKeyConstants.TEAM_NAME, strTeamName.trim());
                                            startActivity(intentTeamChat);*/if (TextUtils.isEmpty(
                            strConsultTeamName
                        )
                    ) {
                        CustomSnackBar.make(
                            chatActBinding.getRoot(), this@ChatActivity, CustomSnackBar.WARNING,
                            errorTeams, CustomSnackBar.TOP, 3000, 0
                        ).show()
                        return@OnClickListener
                    }
                        if (membersList!!.size > 0 && membersList != null) {
                            showTeamMembersDialog(this@ChatActivity, membersList, mConsultProvider)
                        } else {
                            CustomSnackBar.make(
                                chatActBinding.getRoot(), this@ChatActivity, CustomSnackBar.WARNING,
                                errorTeams, CustomSnackBar.TOP, 3000, 0
                            ).show()
                        }
                    })
                } else {
                    binding.tvTeamName.setVisibility(View.GONE)
                }
            } else if (isLeft) {
//                System.out.println("values cominjg herer " + new Gson().toJson(consultMessage));
                binding.leftViewLayout.setVisibility(View.VISIBLE)
                if (consultMessage.getType() == null || MessageType.text.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    binding.messageTextViewLeft.setText(
                        AESUtils.decryptData(
                            consultMessage.getText().trim(), encKey
                        )
                    )
                    binding.messageTextViewLeft.setVisibility(TextView.VISIBLE)
                    binding.messageViewLeft.setVisibility(TextView.VISIBLE)
                } else if (MessageType.Discharged.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    binding.messageTextViewLeft.setText(
                        AESUtils.decryptData(
                            consultMessage.getText().trim(), encKey
                        )
                    )
                    binding.messageTextViewLeft.setVisibility(TextView.VISIBLE)
                    binding.messageViewLeft.setVisibility(TextView.VISIBLE)
                    binding.messageViewLeft.setBackground(getResources().getDrawable(R.drawable.chat_bubble_completed_left))
                    binding.messageTextViewLeft.setTextColor(getResources().getColor(R.color.white))
                    binding.messengerTextViewLeft.setTextColor(getResources().getColor(R.color.white))
                    binding.timeTextViewLeft.setTextColor(getResources().getColor(R.color.chat_time_color))
                } else if (consultMessage.getImageUrl() != null) {
                    val imageUrl: String =
                        AESUtils.decryptData(consultMessage.getImageUrl(), encKey)
                    //                    Log.i(TAG, "Image url left " + imageUrl);
                    if (MessageType.image.toString()
                            .equals(consultMessage.getType(), ignoreCase = true)
                    ) {
                        if (imageUrl.startsWith("gs://")) {
                            val storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl)
                            storageReference.downloadUrl.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadUrl = task.result.toString()
                                    Glide.with(binding.messageImageViewLeft.getContext())
                                        .load(downloadUrl)
                                        .into<Target<Drawable>>(
                                            binding.messageImageViewLeft
                                        )
                                } else {
                                    //                                                Log.w(TAG, "Getting download url was not successful.",
                                    //                                                        task.getException());
                                }
                            }
                        } else {
                            Glide.with(binding.messageImageViewLeft.getContext())
                                .load(imageUrl)
                                .into<Target<Drawable>>(binding.messageImageViewLeft)
                        }
                        binding.imageViewLeftLayout.setVisibility(ImageView.VISIBLE)
                    } else if (MessageType.file.toString()
                            .equals(consultMessage.getType(), ignoreCase = true)
                    ) {
                        if (!imageUrl.startsWith(LOADING_IMAGE_URL)) {
                            binding.fileIconLeft.setImageResource(getFileIcon(consultMessage))
                            binding.fileTextViewLeft.setText(consultMessage.getFilename())
                            binding.fileViewLeft.setVisibility(ImageView.VISIBLE)
                            binding.messageViewLeft.setVisibility(TextView.VISIBLE)
                        } else {
                            Glide.with(binding.messageImageViewLeft.getContext())
                                .load(imageUrl)
                                .into<Target<Drawable>>(binding.messageImageViewLeft)
                            binding.imageViewLeftLayout.setVisibility(ImageView.VISIBLE)
                            binding.messageTextViewLeft.setVisibility(TextView.GONE)
                            binding.messageViewLeft.setVisibility(TextView.GONE)
                            binding.fileViewLeft.setVisibility(ImageView.GONE)
                        }
                    } else if (MessageType.video.toString()
                            .equals(consultMessage.getType(), ignoreCase = true)
                    ) {
                        if (consultMessage.getThumbUrl() == null) {
                            Log.d(TAG, "getThumbUrl Left 1 :" + consultMessage.getThumbUrl())
                            binding.imagePBLeft.setVisibility(View.VISIBLE)
                            binding.imgPlayIconLeft.setVisibility(View.GONE)
                            binding.imageViewLeftLayout.setEnabled(false)
                            Glide.with(binding.messageImageViewLeft.getContext())
                                .load(imageUrl)
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.imagePBLeft.setVisibility(View.GONE)
                                        binding.imageViewLeftLayout.setEnabled(true)
                                        binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.imagePBLeft.setVisibility(View.GONE)
                                        binding.imageViewLeftLayout.setEnabled(true)
                                        binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                        return false
                                    }
                                })
                                .into<Target<Drawable>>(binding.messageImageViewLeft)
                        } else {
//                            Log.d(TAG, "getThumbUrl Left 2 :" + imageUrl);
                            binding.imagePBLeft.setVisibility(View.VISIBLE)
                            binding.imgPlayIconLeft.setVisibility(View.GONE)
                            binding.imageViewLeftLayout.setEnabled(false)
                            Glide.with(binding.messageImageViewLeft.getContext())
                                .load(imageUrl)
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.imagePBLeft.setVisibility(View.GONE)
                                        binding.imageViewLeftLayout.setEnabled(true)
                                        binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.imagePBLeft.setVisibility(View.GONE)
                                        binding.imageViewLeftLayout.setEnabled(true)
                                        binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                        return false
                                    }
                                })
                                .into<Target<Drawable>>(binding.messageImageViewLeft)
                        }
                        /*   if (!imageUrl.startsWith(LOADING_IMAGE_URL)) {
                            if (consultMessage.getThumbUrl() == null) {
                                Log.d(TAG, "getThumbUrl Left 1 :" + consultMessage.getThumbUrl());
                                Glide.with(binding.messageImageViewLeft.getContext())
                                        .load(consultMessage.getImageUrl())
                                        .into(binding.messageImageViewLeft);
                            } else {
                                Log.d(TAG, "getThumbUrl Left 2 :" + consultMessage.getThumbUrl());
                                Glide.with(binding.messageImageViewLeft.getContext())
                                        .load(consultMessage.getThumbUrl())
                                        .into(binding.messageImageViewLeft);
                            }
                        } else {
                            Log.d(TAG, "getThumbUrl Left 3 :" + consultMessage.getThumbUrl());
                            Glide.with(binding.messageImageViewLeft.getContext())
                                    .load(consultMessage.getImageUrl())
                                    .into(binding.messageImageViewLeft);
                        }*/binding.imageViewLeftLayout.setVisibility(ImageView.VISIBLE)
                    }
                }
                val messengerName: String = consultMessage.getName()
                if (messengerName.contains(", ")) {
                    val res: Array<String> = consultMessage.getName().split(", ")
                    if (res.size == 2) {
                        val htmlText =
                            "<h3><b>" + res[0] + "</b></h3>\n" + "<medium>" + res[1] + "</medium>"
                        binding.messengerTextViewLeft.setText(Html.fromHtml(htmlText))
                        binding.messengerImageViewLeft.setText(Html.fromHtml(htmlText))
                    } else {
                        binding.messengerTextViewLeft.setText(consultMessage.getName())
                        binding.messengerImageViewLeft.setText(consultMessage.getName())
                    }
                } else {
                    binding.messengerTextViewLeft.setText(consultMessage.getName())
                    binding.messengerImageViewLeft.setText(consultMessage.getName())
                }

                //binding.messengerImageViewLeft.setText(consultMessage.getName());
                binding.timeTextViewLeft.setText(ChatUtils.getStatusDateFormat(consultMessage.getTime()))
                if (consultMessage.getStatus() == null) {
                    consultMessage.setStatus("Sent")
                }
                //set status
                if (!"Read".equals(consultMessage.getStatus(), ignoreCase = true)) {
                    val path = dbReference.key
                    updateMessageStatus(path)
                }
                if (consultMessage.isUrgent()) {
                    binding.imgMarkAsUrgentViewLeft.setVisibility(View.VISIBLE)
                } else {
                    binding.imgMarkAsUrgentViewLeft.setVisibility(View.GONE)
                }
            } else {
                binding.layoutTextRight.setVisibility(View.VISIBLE)
                if (consultMessage.getType() == null || MessageType.text.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    binding.messageTextViewRight.setText(
                        AESUtils.decryptData(
                            consultMessage.getText().trim(), encKey
                        )
                    )
                    binding.messageTextViewRight.setVisibility(TextView.VISIBLE)
                    binding.messageViewRight.setVisibility(TextView.VISIBLE)
                    binding.messageTextViewRight.setTextColor(getResources().getColor(R.color.title_color))
                    //binding.messageImageViewRight.setVisibility(TextView.VISIBLE);
                } else if (MessageType.Discharged.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    binding.messageTextViewRight.setText(
                        AESUtils.decryptData(
                            consultMessage.getText().trim(), encKey
                        )
                    )
                    binding.messageTextViewRight.setVisibility(TextView.VISIBLE)
                    binding.messageTextViewRight.setTextColor(getResources().getColor(R.color.white))
                    binding.messageViewRight.setVisibility(TextView.VISIBLE)
                    binding.messageViewRight.setBackground(getResources().getDrawable(R.drawable.chat_bubble_completed_right))
                } else if (consultMessage.getImageUrl() != null) {
                    binding.messageTextViewRight.setTextColor(getResources().getColor(R.color.title_color))
                    val imageUrl: String =
                        AESUtils.decryptData(consultMessage.getImageUrl(), encKey)
                    //                    Log.i(TAG, "Image url right " + imageUrl);
                    if (MessageType.image.toString()
                            .equals(consultMessage.getType(), ignoreCase = true)
                    ) {
                        if (imageUrl.startsWith("gs://")) {
                            val storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl)
                            storageReference.downloadUrl.addOnCompleteListener(
                                { task: Task<Uri> ->
                                    if (task.isSuccessful()) {
                                        val downloadUrl: String = task.getResult().toString()
                                        Glide.with(binding.messageImageViewRight.getContext())
                                            .load(downloadUrl)
                                            .into<Target<Drawable>>(
                                                binding.messageImageViewRight
                                            )
                                    } else {
//                                            Log.w(TAG, "Getting download url was not successful.",
//                                                    task.getException());
                                    }
                                })
                        } else {
                            if (binding.messageImageViewRight.getTag() == null) {
                                Glide.with(binding.messageImageViewRight.getContext())
                                    .load(imageUrl)
                                    .into<Target<Drawable>>(binding.messageImageViewRight)
                            } else {
//                                Log.i(TAG, "imageDrawable: " + defaultImageDrawable);
                                binding.messageImageViewRight.setTag(null)
                                Glide.with(binding.messageImageViewRight.getContext())
                                    .load(imageUrl)
                                    .placeholder(defaultImageDrawable)
                                    .into<Target<Drawable>>(binding.messageImageViewRight)
                            }
                        }
                        binding.imageViewRightLayout.setVisibility(ImageView.VISIBLE)
                        binding.imagePBRight.setVisibility(View.GONE)
                    } else if (MessageType.file.toString()
                            .equals(consultMessage.getType(), ignoreCase = true)
                    ) {
                        if (!imageUrl.startsWith(LOADING_IMAGE_URL)) {
                            binding.fileIconRight.setImageResource(getFileIcon(consultMessage))
                            binding.fileTextViewRight.setText(consultMessage.getFilename())
                            binding.fileViewRight.setVisibility(ImageView.VISIBLE)
                            binding.messageViewRight.setVisibility(TextView.VISIBLE)
                        } else {
                            Glide.with(binding.messageImageViewRight.getContext())
                                .load(imageUrl)
                                .into<Target<Drawable>>(binding.messageImageViewRight)
                            binding.imageViewRightLayout.setVisibility(ImageView.VISIBLE)
                        }
                    } else if (MessageType.video.toString()
                            .equals(consultMessage.getType(), ignoreCase = true)
                    ) {
                        if (consultMessage.getThumbUrl() == null) {
//                            Log.d(TAG, "getThumbUrl getImageUrl Right 1 : " + imageUrl);
                            binding.imagePBRight.setVisibility(View.VISIBLE)
                            binding.imgPlayIconRight.setVisibility(View.GONE)
                            binding.imageViewRightLayout.setEnabled(false)
                            Glide.with(binding.messageImageViewRight.getContext())
                                .load(imageUrl)
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.imagePBRight.setVisibility(View.GONE)
                                        binding.imageViewRightLayout.setEnabled(true)
                                        binding.imgPlayIconRight.setVisibility(View.VISIBLE)
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.imagePBRight.setVisibility(View.GONE)
                                        binding.imageViewRightLayout.setEnabled(true)
                                        binding.imgPlayIconRight.setVisibility(View.VISIBLE)
                                        return false
                                    }
                                })
                                .into<Target<Drawable>>(binding.messageImageViewRight)
                        } else {
//                            Log.d(TAG, "getThumbUrl Right 2 : " + imageUrl);
                            binding.imagePBRight.setVisibility(View.VISIBLE)
                            binding.imgPlayIconRight.setVisibility(View.GONE)
                            binding.imageViewRightLayout.setEnabled(false)
                            Glide.with(binding.messageImageViewRight.getContext())
                                .load(imageUrl)
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.imagePBRight.setVisibility(View.GONE)
                                        binding.imageViewRightLayout.setEnabled(true)
                                        binding.imgPlayIconRight.setVisibility(View.VISIBLE)
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.imagePBRight.setVisibility(View.GONE)
                                        binding.imageViewRightLayout.setEnabled(true)
                                        binding.imgPlayIconRight.setVisibility(View.VISIBLE)
                                        return false
                                    }
                                })
                                .into<Target<Drawable>>(binding.messageImageViewRight)
                        }
                        /*  if (!imageUrl.startsWith(LOADING_IMAGE_URL)) {
                            if (consultMessage.getThumbUrl() == null) {
                                Log.d(TAG, "getThumbUrl getImageUrl Right 1 : " + consultMessage.getImageUrl());
                                Glide.with(binding.messageImageViewRight.getContext())
                                        .load(consultMessage.getImageUrl())
                                        .into(binding.messageImageViewRight);
                            } else {
                                Log.d(TAG, "getThumbUrl Right 2 : " + consultMessage.getThumbUrl());
                                Glide.with(binding.messageImageViewRight.getContext())
                                        .load(consultMessage.getThumbUrl())
                                        .into(binding.messageImageViewRight);
                            }
                        } else {
                            Log.d(TAG, "getThumbUrl getImageUrl Right 3 : " + consultMessage.getImageUrl());
                            Glide.with(binding.messageImageViewRight.getContext())
                                    .load(consultMessage.getImageUrl())
                                    .into(binding.messageImageViewRight);
                        }*/binding.imageViewRightLayout.setVisibility(ImageView.VISIBLE)
                    }
                }
                val messengerName: String =
                    PrefUtility.getStringInPref(this, Constants.SharedPrefConstants.NAME, "")
                var strDesignation: String = PrefUtility.getStringInPref(
                    this,
                    Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                    ""
                )
                if ((role == "BD")) {
                    strDesignation = getString(R.string.bedside_provider)
                }
                val labelColor: Int
                if (MessageType.Discharged.toString()
                        .equals(consultMessage.getType(), ignoreCase = true)
                ) {
                    labelColor = getResources().getColor(R.color.white)
                    binding.messengerTextViewRight.setTextColor(getResources().getColor(R.color.white))
                } else {
                    labelColor = getResources().getColor(R.color.text_ash)
                    binding.messengerTextViewRight.setTextColor(getResources().getColor(R.color.drawer_header_txt_color))
                }
                val colorString = String.format("%X", labelColor).substring(2)
                val htmlText = String.format(
                    "<h3><b>$messengerName</b></h3>\n<font color='#%s'><medium>$strDesignation</medium></font>",
                    colorString
                )
                binding.messengerTextViewRight.setText(Html.fromHtml(htmlText))
                binding.messengerImageViewRight.setText(Html.fromHtml(htmlText))

//                binding.messengerTextViewRight.setText("");
                binding.timeTextViewRight.setText(ChatUtils.getStatusDateFormat(consultMessage.getTime()))
                if (consultMessage.getStatus() == null) {
                    consultMessage.setStatus("Sent")
                }

//                Log.i(TAG, "handleChatMessage: MESSAGESTATUSS " + consultMessage.getStatus());
                //
                setMessageReadStatus(consultMessage.getId(), binding.statusTextViewRight)


                /*       if ("Read".equalsIgnoreCase(consultMessage.getStatus())) {
                    binding.statusTextViewRight.setImageResource(R.drawable.ic_double_tick);
                    //ic_double_tick
                    //ic_read - new
                } else if ("Sent".equalsIgnoreCase(consultMessage.getStatus())) {
                    binding.statusTextViewRight.setImageResource(R.drawable.ic_single_tick);
                } else {
                    binding.statusTextViewRight.setImageResource(R.drawable.ic_unread);
                    //ic_delivered - old
                    //ic_unread - new
                }
*/

                // Set urgent message UI
                if (consultMessage.isUrgent()) {
                    binding.imgMarkAsUrgentViewRight.setVisibility(View.VISIBLE)
                } else {
                    binding.imgMarkAsUrgentViewRight.setVisibility(View.GONE)
                }
            }
            //Log.d(TAG, "@@@@@@@@ position " + position);
            if (position == mFirebaseAdapter!!.getItemCount() - 1) {
//                Log.d(TAG, "##################  scroll down to bottom ###################");
                scrollToBottom()
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    private fun setMessageReadStatus(longMessageId: String, statusTextViewRight: ImageView) {
        val chatMessageListStatus = ArrayList<String>()
        //        Log.e("message_path ", "consults/" + strUid + "/messages/" + longMessageId + "/ChatMessageStatusList");
        mFirebaseDatabaseReference!!.child("consults").child(strUid.toString()).child("messages")
            .child(longMessageId).child("ChatMessageStatusList").addValueEventListener(
                object : ValueEventListener {
                    var returnImage: Int = R.drawable.ic_single_tick
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Get Post object and use the values to update the UI
                        chatMessageListStatus.clear()
                        for (ds2: DataSnapshot in dataSnapshot.children) {
                            if (ds2.exists()) {
                                val messageStatusList: ChatMessageStatusModel? = ds2.getValue(
                                    ChatMessageStatusModel::class.java
                                )
                                if (messageStatusList != null) {
                                    chatMessageListStatus.add(
                                        messageStatusList.getStatus().toLowerCase()
                                    )
                                }
                            }
                        }

                        //Log.e("full response", ": " + new Gson().toJson(chatMessageListStatus));
                        if (!chatMessageListStatus.isEmpty()) {
                            if (chatMessageListStatus.contains(MessageTypeRead.sent.toString())) {
                                returnImage = R.drawable.ic_single_tick
                                //   Log.e("tick ", "single tick" + returnImage);
                            }
                            if (chatMessageListStatus.contains(MessageTypeRead.received.toString())) {
                                returnImage = R.drawable.ic_unread
                                //     Log.e("tick ", "dilevered tick" + returnImage);
                            }
                            if (chatMessageListStatus.contains(MessageTypeRead.read.toString())) {
                                returnImage = R.drawable.ic_read
                                //   Log.e("tick ", "read tick" + returnImage);
                            }
                        }
                        statusTextViewRight.setImageResource(returnImage)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
//                        Log.e("message error", ": " + databaseError.getMessage());
                    }
                }
            )
    }

    private fun handleMultipleClicks(view: View) {
        view.isEnabled = false
        mHandler.postDelayed({ view.isEnabled = true }, 500)
    }

    fun getBitmapFromUri(uri: Uri?): Bitmap? {
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        try {
            parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor.getFileDescriptor()
            return BitmapFactory.decodeFileDescriptor(fileDescriptor)
        } catch (e: FileNotFoundException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close()
                } catch (e: IOException) {
//                    Log.e(TAG, "Exception:", e.getCause());
                }
            }
        }
        return null
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        try {
            var width = image.width
            var height = image.height
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 0) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
            return image
        }
    }

    fun getRotatedBitmap(photoPath: String?, bitmap: Bitmap?): Bitmap? {
        var rotatedBitmap: Bitmap? = null
        try {
            val ei = ExifInterface((photoPath)!!)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270f)
                ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                else -> rotatedBitmap = bitmap
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return if (rotatedBitmap == null) {
            bitmap
        } else {
            rotatedBitmap
        }
    }

    private fun getRotateAngle(photoPath: String?): Int {
        var angle = 0
        try {
            val ei = ExifInterface((photoPath)!!)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> angle = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> angle = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> angle = 270
                ExifInterface.ORIENTATION_NORMAL -> angle = 0
                else -> angle = 0
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
        return angle
    }

    fun rotateImage(source: Bitmap?, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            (source)!!, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private fun getBitmap(selectedimg: Uri): Bitmap? {
        var original: Bitmap? = null
        var fileDescriptor: AssetFileDescriptor? = null
        try {
            val options = BitmapFactory.Options()
            options.inSampleSize = 4
            fileDescriptor = getContentResolver().openAssetFileDescriptor(selectedimg, "r")
            original = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options
            )
        } catch (e: FileNotFoundException) {
//            Log.e(TAG, "Exception:", e.getCause());
        } finally {
            try {
                fileDescriptor?.close()
            } catch (e: IOException) {
//                Log.e(TAG, "Exception:", e.getCause());
            }
        }
        return original
    }

    private fun detectKeyboard() {
        try {
            getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(
                OnGlobalLayoutListener {
                    val r = Rect()
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(r)
                    val screenHeight: Int = getWindow().getDecorView().getRootView().getHeight()

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    keypadHeight = screenHeight - r.bottom

                    //                            Log.d(TAG, "keypadHeight = " + keypadHeight);
                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isIsKeyboardShowing) {
                            isIsKeyboardShowing = true
                            scrollToBottom()
                            //                                    Log.i(TAG, "onGlobalLayout: " + isKeyboardShowing);
                        }
                    } else {
                        // keyboard is closed
                        if (isIsKeyboardShowing) {
                            isIsKeyboardShowing = false
                            //                                    Log.i(TAG, "onGlobalLayout: " + isKeyboardShowing);
                        }
                    }
                })
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    fun onBackButtonPressed() {
        if (popup != null && popup.isShowing()) {
            popup.onDismiss()
        } else if (isIsKeyboardShowing) {
            /*InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(iBinder, 0);*/
            hideSoftKeyboard()
        } else {
            onBackPressed()
        }
    }

    fun onKeyboardTouch(): Boolean {
        if (popup != null && popup.isShowing()) {
            popup.onDismiss()
            return true
        }
        return false
    }

    private fun getFileIcon(consultMessage: ConsultMessage?): Int {
        if (consultMessage == null || TextUtils.isEmpty(consultMessage.getFilename())) {
            return R.drawable.ic_generic
        }
        val fileName: String = consultMessage.getFilename().toLowerCase()
        if (fileName.contains(".txt")) {
            return R.drawable.ic_txt
        } else if (fileName.contains(".doc") || fileName.contains(".docx")) {
            return R.drawable.ic_doc
        } else if (fileName.contains(".pdf")) {
            return R.drawable.ic_pdf
        } else if (fileName.contains(".xls") || fileName.contains(".xlsx")) {
            return R.drawable.ic_xls
        } else return if (fileName.contains(".ppt") || fileName.contains(".pptx")) {
            R.drawable.ic_ppt
        } else {
            R.drawable.ic_generic
        }
    }

    fun clearNotifications(notificationId: Long?) {
        when (notificationId) {
            Constants.NotificationIds.NOTIFICATION_ID -> {
                NotificationHelper(this).clearNotification(Constants.NotificationIds.NOTIFICATION_ID)
            }
            Constants.NotificationIds.MSG_NOTIFICATION_ID -> {
                NotificationHelper(this).clearNotification(Constants.NotificationIds.MSG_NOTIFICATION_ID)
            }
            else -> {
                NotificationHelper(this).clearNotification(notificationId)
            }
        }
    }

    fun showDownloadPB(text: String?, taskType: String) {
        dismissDownloadPB()
        try {
            downloadPB = DownloadProgressDialog(this)
            downloadPB.setCancelable(false)
            downloadPB.setText(text)
            downloadPB.setProgress(0)
            downloadPB.setCancelBtnListener { view ->
                if (taskType.equals(
                        TaskType.upload.toString(),
                        ignoreCase = true
                    )
                ) {
                    cancelUploadingTask()
                } else {
                    cancelFileDownloadingTask()
                }
                dismissDownloadPB()
            }
            var isDestyoed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestyoed = isDestroyed()
            }
            if (!isFinishing() && !isDestyoed) {
                if (downloadPB != null && !downloadPB.isShowing()) {
                    downloadPB.show()
                }
            }
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    fun dismissDownloadPB() {
        try {
            var isDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (isDestroyed()) {
                    isDestroyed = true
                }
            }
            if (!isFinishing() && !isDestroyed && (downloadPB != null) && downloadPB.isShowing()) {
                downloadPB.dismiss()
            }
            downloadPB = null
        } catch (e: Exception) {
//            Log.e(TAG, "Exception:", e.getCause());
        }
    }

    fun handleMultipleClick(view: View) {
        view.isEnabled = false
        view.isClickable = false
        mHandler.postDelayed({
            view.isClickable = true
            view.isEnabled = true
        }, 500)
    }

    private fun showPatientRemovedDialog(status: String) {
        val dialog: Array<CustomDialog?> = arrayOf(null)
        var isDestroyed = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            isDestroyed = isDestroyed()
        }
        val finalIsDestroyed = isDestroyed
        val positiveBtnListener =
            View.OnClickListener { view: View? ->
                try {
                    if ((dialog.get(0) != null) && dialog.get(0)
                            .isShowing() && !finalIsDestroyed && !isFinishing()
                    ) {
                        dialog.get(0).dismiss()
                        dialog.get(0) = null
                    }
                    startActivity(
                        Intent(this@ChatActivity, HomeActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .putExtra(Constants.IntentKeyConstants.TARGET_PAGE, "pending")
                    )
                    finish()
                } catch (e: Exception) {
//                Log.e(TAG, "Exception:", e.getCause());
                }
            }
        if ((dialog[0] == null || !dialog[0].isShowing()) && !isFinishing() && !isDestroyed) {
            val message: String
            if (status.equals(
                    java.lang.String.valueOf(Constants.PatientStatus.Active),
                    ignoreCase = true
                )
            ) {
                if ((mConsultProvider.getStatus().equals(Constants.PatientStatus.Handoff)
                            || mConsultProvider.getStatus()
                        .equals(Constants.PatientStatus.HandoffPending))
                ) {
                    val strRole: String =
                        (PrefUtility.getStringInPref(this, Constants.SharedPrefConstants.ROLE, ""))
                    val strDesignation: String = (PrefUtility.getStringInPref(
                        this,
                        Constants.SharedPrefConstants.R_PROVIDER_TYPE,
                        ""
                    ))
                    if (((strRole == "RD")) && (!strDesignation.equals(
                            "MD/DO",
                            ignoreCase = true
                        ))
                    ) {
                        message = getString(R.string.patient_handed_off_to_other_grp)
                    } else {
                        message = getString(R.string.handoff_request_already_accepted_msg)
                    }
                } else {
                    message = getString(R.string.consultation_request_already_accepted_msg)
                }
            } else {
                message = getString(R.string.consultation_request_completed)
            }
            //            dialog[0] = UtilityMethods.showDialog(this, getString(R.string.req_not_available), message, false, R.string.ok, positiveBtnListener, -1, null, -1);
            dialog[0] = UtilityMethods.showDialog(
                this,
                "",
                message,
                false,
                R.string.ok,
                positiveBtnListener,
                -1,
                null,
                -1,
                false
            )
            dialog[0].setPositiveButtonDrawable(R.drawable.dialog_single_btn_drawable)
        }
    }

    private fun getCompletionViewDrawable(drawable: Int): Drawable {
        val unwrappedDrawable = AppCompatResources.getDrawable(this, drawable)
        val wrappedDrawable = DrawableCompat.wrap((unwrappedDrawable)!!)
        DrawableCompat.setTint(
            wrappedDrawable,
            getResources().getColor(R.color.color_complete_consultation)
        )
        return wrappedDrawable
    }

    private fun setPatientHistoryAdapter() {
        if (patientHistoryList == null) {
            patientHistoryList = ArrayList<PatientHistory?>()
        }
        historyAdapter = PatientChatHistoryAdapter(this, patientHistoryList)
        chatActBinding.historyList.setLayoutManager(LinearLayoutManager(this))
        chatActBinding.historyList.hasFixedSize()
        chatActBinding.historyList.setNestedScrollingEnabled(false)
        chatActBinding.historyList.setAdapter(historyAdapter)
    }//                    UtilityMethods.showErrorSnackBar(chatActBinding.getRoot(), errMsg, Snackbar.LENGTH_LONG);//                        Log.d(TAG, "getPatientChatHistory" + new Gson().toJson(response));//                System.out.println("history response " + response);

    //            UtilityMethods.showErrorSnackBar(chatActBinding.getRoot(), getString(R.string.patient_history_error_due_to_internet), Snackbar.LENGTH_LONG);
    //showProgressBar(getString(R.string.fetching_patient_history_pb_msg));
    private val patientChatHistory: Unit
        private get() {
            if (!UtilityMethods.isInternetConnected(this)) {
                //            UtilityMethods.showErrorSnackBar(chatActBinding.getRoot(), getString(R.string.patient_history_error_due_to_internet), Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    chatActBinding.getRoot(),
                    this@ChatActivity,
                    CustomSnackBar.WARNING,
                    getString(R.string.patient_history_error_due_to_internet),
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
                return
            }
            //showProgressBar(getString(R.string.fetching_patient_history_pb_msg));
            val providerId: Long = PrefUtility.getProviderId(this)
            val token: String = PrefUtility.getToken(this)
            val patientId: Long =
                if (!TextUtils.isEmpty(java.lang.String.valueOf(mConsultProvider.getPatientsId()))) java.lang.String.valueOf(
                    mConsultProvider.getPatientsId()
                ).toLong() else 0
            chatViewModel.getPatientChatHistory(providerId, token, patientId)
                .observe(this, object : Observer<PatientHistoryResponse?> {
                    override fun onChanged(response: PatientHistoryResponse?) {
                        dismissProgressBar()
                        //                System.out.println("history response " + response);
                        if ((response != null) && (response.getStatus() != null) && response.getStatus()) {
                            if (response.getPatientHistoryList() != null) {
                                patientHistoryListBackup = response.getPatientHistoryList()
                                //                        Log.d(TAG, "getPatientChatHistory" + new Gson().toJson(response));
                                setPatientChatHistoryView()
                            }
                        } else {
                            var errMsg: String? = null
                            if (response != null) {
                                errMsg = ErrorMessages.getErrorMessage(
                                    this,
                                    response.getErrorMessage(),
                                    Constants.API.getPatientHistory
                                )
                            }
                            //                    UtilityMethods.showErrorSnackBar(chatActBinding.getRoot(), errMsg, Snackbar.LENGTH_LONG);
                            CustomSnackBar.make(
                                chatActBinding.getRoot(),
                                this@ChatActivity,
                                CustomSnackBar.WARNING,
                                errMsg,
                                CustomSnackBar.TOP,
                                3000,
                                0
                            ).show()
                        }
                    }
                })
        }

    private fun setPatientChatHistoryView() {
        if ((patientHistoryListBackup == null) || patientHistoryListBackup!!.isEmpty() || (patientHistoryListBackup!!.size == 1)) {
            mFirebaseAdapter!!.notifyDataSetChanged()
            chatActBinding.rlViewDetails.setVisibility(View.VISIBLE)
            return
        }
        if (sortResetFlag) {
            Collections.sort(patientHistoryListBackup,
                Comparator<Any?> { patientHistory1, patientHistory2 ->
                    if ((patientHistory1 == null) || (patientHistory2 == null
                                ) || (patientHistory1.getDischargeTime() == null) || (patientHistory2.getDischargeTime() == null)
                    ) {
                        Int.MIN_VALUE
                    } else patientHistory1.getDischargeTime()
                        .compareTo(patientHistory2.getDischargeTime())
                })
        }

//        System.out.println("patientbackupval " + new Gson().toJson(patientHistoryListBackup));
        if (patientHistoryList == null) {
            patientHistoryList = ArrayList<PatientHistory?>()
        }
        patientHistoryList!!.clear()
        patientHistory = null
        for (history: PatientHistory? in patientHistoryListBackup) {
//            System.out.println("consultmessageval " + new Gson().toJson(history));
            if (((history != null) && !TextUtils.isEmpty(history.getStatus())
                        && history.getStatus()
                    .equalsIgnoreCase(Constants.PatientStatus.Completed.toString()))
            ) {
                patientHistoryList!!.add(history)
                //                Log.d(TAG, "patientHistoryListCompleted" + new Gson().toJson(patientHistoryList));
            } else {
                patientHistory = history
                //                Log.d(TAG, "patientHistoryListDischarge" + new Gson().toJson(patientHistory));
            }
        }
        if (patientHistory == null) {
            chatActBinding.rlViewDetails.setVisibility(View.GONE)
        } else {
            chatActBinding.rlViewDetails.setVisibility(View.VISIBLE)
        }
        if (!patientHistoryList!!.isEmpty()) {
            chatActBinding.historyList.setVisibility(View.VISIBLE)
            chatActBinding.selectedPatient.setVisibility(View.GONE)
            handleChatEditView(isCompleted, isInvited)
            historyAdapter.notifyDataSetChanged()
        }
        mFirebaseAdapter!!.notifyDataSetChanged()
    }

    fun onClickChatHistory(position: Int, viewHolder: PatientChatHistoryAdapter.ViewHolder?) {
//        Log.d(TAG, "onClickChatHistory: " + position);
        if (patientHistoryList == null || patientHistoryList!!.isEmpty()) {
            return
        }
        if (patientHistoryList!!.size > position) {
            if (patientHistory != null && patientHistory?.getId()
                    ?.equals(patientHistoryList!![position].getId()) == true
            ) {
                return
            }
            patientHistory = patientHistoryList!![position]
        }
        if (patientHistory?.getInviteTime() != null) {
//            Log.d(TAG, "patient Joining time : " + patientHistory.getInviteTime() + "- " + ChatUtils.getStatusDateFormat(patientHistory.getInviteTime()));
        }
        if (patientHistory?.getDischargeTime() != null) {
//            Log.d(TAG, "patient Discharge time : " + patientHistory.getDischargeTime() + " - " + ChatUtils.getStatusDateFormat(patientHistory.getDischargeTime()));
        }
        isShowingSelectedChatHisotry = true
        chatActBinding!!.historyList.setVisibility(View.GONE)
        chatActBinding!!.rlViewDetails.setVisibility(View.VISIBLE)
        chatActBinding!!.selectedPatient.setVisibility(View.VISIBLE)
        setSelectedPatientValues(position, patientHistory)
        handleChatEditView(true, false)
        mFirebaseAdapter!!.notifyDataSetChanged()
        scrollToBottom()

    }

    override fun onCloseBtnClick(position: Int) {
        isShowingSelectedChatHisotry = false
        sortResetFlag = false
        setPatientChatHistoryView()
        scrollToBottom()

    }

    private fun isIncludeChat(
        patientHistory: PatientHistory?,
        consultMessage: ConsultMessage?
    ): Boolean {
        if (patientHistoryListBackup!!.size == 1) {
            return true
        }
        if (patientHistory == null || consultMessage == null) {
            return false
        }
        val status: String? = patientHistory.getStatus()
        val inviteTime: Long? = patientHistory.getInviteTime()
        val dischargeTime: Long? = patientHistory.getDischargeTime()
        val joiningTime: Long? = patientHistory.getJoiningTime()


        if (status != null) {

            if (!status.equals(Constants.PatientStatus.Completed.toString(), ignoreCase = true)) {

                return if (inviteTime != null && (consultMessage.getTime() / 1000) >= (inviteTime / 1000)) {
                    true
                } else joiningTime != null && (consultMessage.getTime() / 1000) >= (inviteTime / 1000)
            } else if (status.equals(
                    Constants.PatientStatus.Completed.toString(),
                    ignoreCase = true
                ) && dischargeTime != null
            ) {
                return (inviteTime != null) && ((consultMessage.getTime() / 1000) >= (inviteTime / 1000)
                        ) && ((consultMessage.getTime() / 1000) <= (dischargeTime / 1000))

            }
        }
        return false
    }

    private fun scrollToBottom() {
//        if (isKeyboardShowing) {
//            chatActBinding.messageEditText.requestFocus();
//        }
        chatActBinding!!.scrollView.post(Runnable {
            chatActBinding!!.scrollView.fullScroll(View.FOCUS_DOWN)
            if (isIsKeyboardShowing) {
                chatActBinding!!.messageEditText.requestFocus()
            }
        })
    }

    private fun setSelectedPatientValues(position: Int, patientHistory: PatientHistory?) {
        val counterTxtView: TextView = chatActBinding.selectedPatient.findViewById(R.id.counterTxt)
        val chatHistoryTxtView: TextView =
            chatActBinding!!.selectedPatient.findViewById(R.id.chatHistoryMsgTxt)
        val timeTxtView: TextView =
            chatActBinding!!.selectedPatient.findViewById(R.id.chatHistoryTimeTxt)
        val iconImgView: ImageView =
            chatActBinding!!.selectedPatient.findViewById(R.id.chatHistoryIcon)
        val closeIcon: ImageView = chatActBinding!!.selectedPatient.findViewById(R.id.closeIcon)
        if (patientHistory != null) {


            var prefix: String = getString(R.string.consultation_completed)
            counterTxtView.text = (position + 1).toString() + "."
            if (!TextUtils.isEmpty(patientHistory.getDischargeMessage())) {
                if (!TextUtils.isEmpty(patientHistory.getRdProviderName())) {
                    prefix =
                        prefix + " by " + "<font color=black><b>" + patientHistory.getRdProviderName() + "</b></font>" + " with notes: "
                } else {
                    prefix = "$prefix with notes: "
                }
                val str = prefix + patientHistory.getDischargeMessage()
                chatHistoryTxtView.text = Html.fromHtml(str)
            } else {
                chatHistoryTxtView.text = prefix
            }
            if (patientHistory.getDischargeTime() != null) {
                timeTxtView.setText(ChatUtils().getStatusDateFormat(patientHistory.getDischargeTime()))
            } else {
                timeTxtView.text = ""
            }
            closeIcon.visibility = View.VISIBLE
            iconImgView.visibility = View.GONE
            closeIcon.setOnClickListener { onCloseBtnClick(position) }
        }
    }

    private fun handleFileUploadError(error: String) {
        try {
            dismissDownloadPB()
            uploadInProgress = false
            isImageUploadFailed = true
            if (!TextUtils.isEmpty(error)) {
//                UtilityMethods.showErrorSnackBar(chatActBinding.getRoot(), error, Snackbar.LENGTH_LONG);
                CustomSnackBar.make(
                    chatActBinding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    error,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
            }
            mFirebaseAdapter!!.notifyItemChanged(mFirebaseAdapter!!.getItemCount() - 1)
        } catch (e: Exception) {

        }
    }

    fun getTeamMemberDetails(patientId: Long, teamName: String?) {
        membersList!!.clear()

        chatViewModel.getMemberList(patientId, teamName).observe(this) { response ->
            dismissProgressBar()
            if ((response != null) && (response.getStatus() != null) && response.getStatus()) {

                if (response.getTeamDetails().getMembers() != null) {
                    membersList.addAll(response.getTeamDetails().getMembers())
                }
            } else if (!TextUtils.isEmpty(response.getErrorMessage()) && response.getErrorMessage() != null) {
                dismissProgressBar()
                errorTeams = response.getErrorMessage()

            } else {
                dismissProgressBar()
                errorTeams = getString(R.string.api_error)
            }
        }
    }

    private fun getPatientDetails(uid: Long) {
        chatViewModel.getPatientDetails(uid).observe(this) { response ->

            val patID: Long = mConsultProvider?.getPatientsId()
            // Because of Custom Model class
            if (response != null && response.getStatus()) {
                val gson: Gson = Gson()

                val teamName: String = "Team " + response.getPatient().getTeamName()

                strConsultTeamName = teamName
                if (mFirebaseAdapter != null) {
                    mFirebaseAdapter!!.notifyDataSetChanged()
                }
                getTeamMemberDetails(patID, teamName)
            } else if (!TextUtils.isEmpty(response.getErrorMessage()) && response.getErrorMessage() != null) {
                dismissProgressBar()
                val errMsg: String = ErrorMessages().getErrorMessage(
                    this,
                    java.lang.String.valueOf(response.getErrorMessage()),
                    Constants.API.getPatientDetails
                )
                CustomSnackBar.make(
                    chatActBinding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()

            } else {
                dismissProgressBar()
                val strErrMsg: String = getString(R.string.api_error)

            }
        }
    }

    /**
     * Displays the mark/unmark as urgent/view details pop up
     *
     * @param context
     * @param v
     */
    private fun showMarkUnMarkAsUrgentPopup(
        context: Activity,
        v: View,
        consultMessage: ConsultMessage,
        binding: ItemMessageBinding,
        lastItem: Boolean
    ) {

//        if(chatIsLive){
//            Log.e(TAG, "showMarkUnMarkAsUrgentPopupchatIsLive:true--> "+chatIsLive );
//        }else{
//            Log.e(TAG, "showMarkUnMarkAsUrgentPopupchatIsLive:false--> "+chatIsLive );
//        }

        // UI variables
        val llMarkAsUrgent: LinearLayout
        val llViewDetails: LinearLayout
        val img_mark_as_urgent: ImageView
        val txtMarkAsUrgent: TextView

        // Inflate the chat_popup_layout.xml
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = layoutInflater.inflate(R.layout.chat_popup_layout, null)
        llMarkAsUrgent = layout.findViewById(R.id.ll_mark_as_urgent)
        llViewDetails = layout.findViewById(R.id.ll_view_details)
        img_mark_as_urgent = layout.findViewById(R.id.img_mark_as_urgent)
        txtMarkAsUrgent = layout.findViewById(R.id.txtMarkAsUrgent)


        // Creating the PopupWindow
        popupWindow = PopupWindow(context)
        popupWindow!!.contentView = layout

        popupWindow!!.height = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        popupWindow!!.width = v.measuredWidth

        popupWindow!!.isFocusable = true

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        val OFFSET_X = -20
        val OFFSET_Y = 50

        //Clear the default translucent background
        popupWindow!!.setBackgroundDrawable(null)

        // Displaying the popup at the specified location

        if (lastItem) {

            if (v.measuredHeight > 300) {

                popupWindow!!.showAsDropDown(v, 0, -((v.measuredHeight) + 250), Gravity.NO_GRAVITY)
            } else {
                popupWindow!!.showAsDropDown(v, 0, -((v.measuredHeight) + 200), Gravity.NO_GRAVITY)
            }
        } else {

            popupWindow!!.showAsDropDown(v, 0, 60, Gravity.NO_GRAVITY)
        }


        // To dim the background
        val container = popupWindow!!.contentView.rootView
        val context1 = popupWindow!!.contentView.context
        val wm = context1.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)


        // Urgent image marking for sender
        // If the user marked message as urgent - Display unmark as urgent UI
        // If the user unmarked urgent message - Display mark as urgent UI
        if (consultMessage.isUrgent()) {
            img_mark_as_urgent.setImageResource(R.drawable.ic_star_outline_24)
            txtMarkAsUrgent.text = context.resources.getString(R.string.unmark_as_urgent)
        } else {
            img_mark_as_urgent.setImageResource(R.drawable.mark_as_urgent_red)
            txtMarkAsUrgent.text = context.resources.getString(R.string.mark_as_urgent)
        }

        // Mark as urgent API hit on click listener
        llMarkAsUrgent.setOnClickListener({ v1: View? ->


            // Check whether urgent is true/false
            var urgent: Boolean = true
            urgent = !consultMessage.isUrgent()

            //Show progress bar
            showProgressBar()

            //Trigger Mark/Unmark as urgent API
            sendMarkAsUrgentAPICall(consultMessage, binding, urgent)
        })
        // View recipients activity intent click listner
        llViewDetails.setOnClickListener({ v1: View? ->
            showProgressBar()

            // For resolving multiple intent passing to view details screen fix
            startViewDetailsListener(consultMessage.getId(), (strUid)!!)
            mConsultMessage = consultMessage
        })

    }

    /**
     * SendchatMessage API call for send mark as urgent updation
     *
     * @param consultMessage
     */
    private fun sendMarkAsUrgentAPICall(
        consultMessage: ConsultMessage,
        itemMessageBinding: ItemMessageBinding,
        isUrgent: Boolean
    ) {




        // New sendchatMessage API call input request object
        try {
            if (!UtilityMethods().isInternetConnected(this)) {

                CustomSnackBar.make(
                    chatActBinding?.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP,
                    3000,
                    0
                )?.show()
                return
            }
            var type = ""
            var subType: String = ""
            if (consultMessage.getType().equals("text",ignoreCase = true)) {
                type = "Text"
                subType = "Text"
            } else if (consultMessage.getType().equals("image",ignoreCase = true)) {
                type = "Attachment"
                subType = "Image"
            } else if (consultMessage.getType().equals("file",ignoreCase = true)) {
                type = "Attachment"
                subType = "File"
            } else if (consultMessage.getType().equals("video",ignoreCase = true)) {
                type = "Attachment"
                subType = "Video"
            }

            // Provider Id
            val providerID: Long = PrefUtility().getLongInPref(
                this,
                Constants.SharedPrefConstants.USER_ID,
                -1)

            // Input request object
            val sendChatMessageInputRequestModel = SendChatMessageInputRequestModel()

            // Fetching token
            val token: String? = PrefUtility().getStringInPref(
                this,
                Constants.SharedPrefConstants.TOKEN,
                ""
            )

            //Newly added
            sendChatMessageInputRequestModel.setSenderId(providerID.toString().toInt())
            if (type.equals("Text", ignoreCase = true)) {
                sendChatMessageInputRequestModel.setMessage(consultMessage.getText())
                sendChatMessageInputRequestModel.setTitle("Yes")
            } else {
                sendChatMessageInputRequestModel.setMessage(consultMessage.getImageUrl())
            }
            sendChatMessageInputRequestModel.setType(type)
            sendChatMessageInputRequestModel.setSubType(subType)
            sendChatMessageInputRequestModel.setPatientId(mConsultProvider?.getPatientsId())

            sendChatMessageInputRequestModel.setSenderName(consultMessage.getName())

            sendChatMessageInputRequestModel.setImportant(false)
            sendChatMessageInputRequestModel.setTime(consultMessage.getTime())

            // Newly added
            if (consultMessage.getMessageId() ) {
                consultMessage.setMessageId(messageId)
                messageId = 0
            }
            sendChatMessageInputRequestModel.setId(consultMessage.getMessageId())
            sendChatMessageInputRequestModel.setUrgent(isUrgent)


            sendChatMessageInputRequestModel.setToken(token)
            sendChatMessageInputRequestModel.setProviderId(providerID)

            // Trigger API call for SendChatMessage - default google api call
            chatViewModel.sendChatMessageCall(
                sendChatMessageInputRequestModel,
                consultMessage.getId()
            ).observe(this) { sendChatMessageOutuputResponseModel1 ->
                if ((sendChatMessageOutuputResponseModel1 != null) && (sendChatMessageOutuputResponseModel1.getStatus() != null) && sendChatMessageOutuputResponseModel1.getStatus()) {

                    // Firebase uid
                    val mFirebaseUserProviderUid: String? = PrefUtility().getFireBaseUid(this)

//
                    // Success response


                    if (sendChatMessageOutuputResponseModel1.getStatus()) {
                        runOnUiThread {

                            // Dismiss progress bar
                            dismissProgressBar()
                            //Dismiss popup
                            if (popupWindow!!.isShowing()) {
                                popupWindow!!.dismiss()

                                // Pushing the urgent parameter to firebase
//
                                mFirebaseDatabaseReference!!.child("consults")
                                    .child(strUid.toString())
                                    .child("messages")
                                    .child(java.lang.String.valueOf(consultMessage.getId()))
                                    .child("urgent").setValue(isUrgent)
                            }
                        }
                    } else {

                        // Failure response
//                    String errMsg = ErrorMessages.getErrorMessage(ChatActivity.this, String.valueOf(sendChatMessageOutuputResponseModel.getErrorMessage()), Constants.API.getHospital);
                        val errMsg: String? = ErrorMessages().getErrorMessage(
                            this,
                            java.lang.String.valueOf(sendChatMessageOutuputResponseModel1.getErrorMessage()),
                            Constants.API.getHospital
                        )
                        runOnUiThread {

                            // Dismiss progress bar
                            dismissProgressBar()
                            //Dismiss popup
                            if (popupWindow!!.isShowing()) {
                                popupWindow!!.dismiss()
                            }
                            if (errMsg == null || errMsg.equals("null", ignoreCase = true)) {
                                CustomSnackBar.make(
                                    chatActBinding?.getRoot(),
                                    this,
                                    CustomSnackBar.WARNING,
                                    Constants.API_ERROR,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0
                                ).show()
                            } else {
                                CustomSnackBar.make(
                                    chatActBinding?.getRoot(),
                                    this,
                                    CustomSnackBar.WARNING,
                                    errMsg,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0
                                )?.show()
                            }
                        }
                    }
                } else {
                    val errMsg: String? = ErrorMessages().getErrorMessage(
                        this,
                        sendChatMessageOutuputResponseModel1.getErrorMessage(),
                        Constants.API.startCall
                    )
                    CustomSnackBar.make(
                        chatActBinding?.getRoot(),
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
        } catch (e: Exception) {
            dismissProgressBar()
            runOnUiThread {
                if (popupWindow!!.isShowing()) {
                    popupWindow!!.dismiss()
                    CustomSnackBar.make(
                        chatActBinding?.getRoot(),
                        this,
                        CustomSnackBar.WARNING,
                        getString(R.string.api_error),
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }

        }

//
    }

    /**
     * Send chat message Retrofit
     *
     * @param sendChatMessageInputRequestModel
     */
    private fun sendChatMessagesRetroCall(
        sendChatMessageInputRequestModel: SendChatMessageInputRequestModel,
        consultMessageId: String
    ) {
        sendApiSuccessful = true
        chatViewModel.sendChatMessageCall(sendChatMessageInputRequestModel, "")
            .observe(this@ChatActivity) { sendChatMessageOutuputResponseModel1 ->
                if ((sendChatMessageOutuputResponseModel1 != null) && (sendChatMessageOutuputResponseModel1.getStatus() != null) && sendChatMessageOutuputResponseModel1.getStatus()) {
//
                    lastMessageKey = consultMessageId

                    messageId =
                        sendChatMessageOutuputResponseModel1.getChatMessages().getId().toLong()
                    mFirebaseDatabaseReference!!.child("consults").child(strUid.toString())
                        .child("messages").child(
                            lastMessageKey!!
                        ).child("messageId").setValue(
                            sendChatMessageOutuputResponseModel1.getChatMessages().getId().toLong()
                        )
                    val messageStatusList: ArrayList<ChatMessageStatusModel> =
                        ArrayList<ChatMessageStatusModel>(sendChatMessageOutuputResponseModel1.getChatMessageStatusList())
                    for (i in messageStatusList.indices) {
                        messageStatusList.get(i).setTime(System.currentTimeMillis())
                        mFirebaseDatabaseReference!!.child("consults").child(strUid.toString())
                            .child("messages")
                            .child(lastMessageKey!!).child("ChatMessageStatusList")
                            .child(
                                sendChatMessageOutuputResponseModel1.getChatMessageStatusList()
                                    .get(i)
                                    .getReceiverId()
                            )
                            .setValue(messageStatusList.get(i))
                            .addOnSuccessListener { // Write was successful!
                                // ...
                                // Toast.makeText(ChatActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                sendApiSuccessful = false
                                dismissProgressBar()
                            }
                            .addOnFailureListener(object :
                                OnFailureListener {
                                override fun onFailure(e: Exception) {
                                    // Write failed
                                    // ...
                                    Toast.makeText(this@ChatActivity, "Error", Toast.LENGTH_SHORT)
                                        .show()
                                    sendApiSuccessful = false
                                    dismissProgressBar()
                                    //                                    Log.e("Error chat", e.toString());
                                }
                            })
                    }
                } else {
                    val errMsg: String? = ErrorMessages().getErrorMessage(
                        this,
                        sendChatMessageOutuputResponseModel1.getErrorMessage(),
                        Constants.API.startCall
                    )
                    CustomSnackBar.make(
                        chatActBinding?.getRoot(),
                        this,
                        CustomSnackBar.WARNING,
                        errMsg,
                        CustomSnackBar.TOP,
                        3000,
                        0
                    )?.show()
                }
            }
    }

    private enum class MessageTypeRead {
        system, text, Discharged, Exit, image, file, video, sent, received, read
    }

    private enum class MessageType {
        system, text, Discharged, Exit, image, file, video
    }

    private enum class TaskType {
        upload, download
    }

    internal inner class TaskConsultMessage(consultMessage: ConsultMessage, filePath: String) :
        AsyncTask<Void?, Void?, ConsultMessage>() {
        private val consultMessage: ConsultMessage
        private var filePath = ""
        override fun onPreExecute() {
            super.onPreExecute()
            //            Log.d(TAG, "onPreExecute: ");
            var message: String? = ""
            //            if (consultMessage.getType() == "image") {
            if (consultMessage.getType().equals("image")) {
                message = getString(R.string.downloading_image_pb_msg)
            } else {
                message = getString(R.string.downloading_file_pb_msg)
            }
            showDownloadPB(message, TaskType.download.toString())
        }

        protected override fun doInBackground(vararg p0: Void?): ConsultMessage? {
//            Log.d(TAG, "doInBackground: ");
            downloadFile(consultMessage.getImageUrl(), filePath)
            consultMessage.setImageUrl(filePath)
            return consultMessage
        }

        override fun onCancelled(consultMessage: ConsultMessage) {
            super.onCancelled(consultMessage)
            mFileDownloadTask = null
        }

        override fun onPostExecute(consultMessageA: ConsultMessage) {
//            Log.d(TAG, "onPostExecute: ");
            dismissDownloadPB()
            processDownloadedFile(consultMessageA)
        }

        init {
            this.consultMessage = consultMessage
            this.filePath = filePath
        }
    }

    private inner class AsyncTaskUpdateUnread() :
        AsyncTask<ConsultMessage?, String?, String?>() {
        override fun doInBackground(vararg p0: ConsultMessage?): String? {
            val consultMessage: ConsultMessage = consultMessages[0]
            //            Log.e(TAG, "doInBackground: consultMessage-->" + new Gson().toJson(consultMessage));
            for (s: String in membersMap!!.keys) {
//                Log.d(TAG, s + " : " + membersMap.get(s));
                mFirebaseDatabaseReference!!.child("providers").child(s).child("active").child(
                    (membersMap!![s])!!
                )
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                                Log.i(TAG, "onDataChange: ");
                            val consultProviderA: ConsultProvider? =
                                dataSnapshot.getValue(ConsultProvider::class.java)
                            //                                Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
//                                Object val = dataSnapshot.getValue();
                            if (consultProviderA != null) {
                                if (consultMessage.getText() != null) {
                                    consultProviderA.setText(consultMessage.getText())
                                } else {
                                    //consultProviderA.setText(consultMessage.getType() + " added");
                                    //int unread = consultProviderA.getUnread()+1;
                                    consultProviderA.setText(getString(R.string.attachment).toString() + ":" + 1 + " " + consultMessage.getType())
                                }
                                mFirebaseDatabaseReference!!.child("providers").child(s)
                                    .child("active").child(
                                        (membersMap!![s])!!
                                    ).child("text").setValue(consultProviderA.getText())
                                consultProviderA.setTime(consultMessage.getTime())
                                mFirebaseDatabaseReference!!.child("providers").child(s)
                                    .child("active").child(
                                        (membersMap!![s])!!
                                    ).child("time").setValue(consultMessage.getTime())
                                consultProviderA.setMsgName(mUsername)
                                mFirebaseDatabaseReference!!.child("providers").child(s)
                                    .child("active").child(
                                        (membersMap!![s])!!
                                    ).child("msgName").setValue(mUsername)
                                if (s.equals(mProviderUid, ignoreCase = true)) {
                                    consultProviderA.setUnread(0)
                                    if (consultProviderA.getUnread() > 0) {
                                        mFirebaseDatabaseReference!!.child("providers").child(s)
                                            .child("active").child(
                                                (membersMap!![s])!!
                                            ).child("unread").setValue(0)
                                    }
                                } else {

                                }
                            }
                            mHandler?.post(Runnable {
                                uploadInProgress = false

                            })


                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
            }
            val providerID: Long = PrefUtility().getLongInPref(
                this,
                Constants.SharedPrefConstants.USER_ID,
                -1
            )
            if ((providerID != -1L) && (membersMap != null) && (membersMap!!.size > 0)) {
                val token: String? = PrefUtility().getStringInPref(
                    this,
                    Constants.SharedPrefConstants.TOKEN,
                    ""
                )
                var message: String = ""
                if (consultMessage.getText() != null) {

                    message = "message"
                } else {
                    message = "attachment"

                }

                try {

                    // New sendchatMessage API call input request object
                    var type = ""
                    var subType: String = ""
                    if (consultMessage.getType().equals("text",ignoreCase = true)) {
                        type = "Text"
                        subType = "Text"
                    } else if (consultMessage.getType().equals("image",ignoreCase = true)) {
                        type = "Attachment"
                        subType = "Image"
                    } else if (consultMessage.getType().equals("file",ignoreCase = true)) {
                        type = "Attachment"
                        subType = "File"
                    } else if (consultMessage.getType().equals("video",ignoreCase = true)) {
                        type = "Attachment"
                        subType = "Video"
                    }

                    // Input request object

                    val sendChatMessageInputRequestModel = SendChatMessageInputRequestModel()
                    sendChatMessageInputRequestModel.setSenderId(providerID.toString().toInt())
                    if (type.equals("Text", ignoreCase = true)) {
                        sendChatMessageInputRequestModel.setMessage(consultMessage.getText())
                        sendChatMessageInputRequestModel.setTitle("Yes")
                    } else {
                        sendChatMessageInputRequestModel.setMessage(consultMessage.getImageUrl())
                    }
                    sendChatMessageInputRequestModel.setType(type)
                    sendChatMessageInputRequestModel.setSubType(subType)
                    sendChatMessageInputRequestModel.setPatientId(mConsultProvider.getPatientsId())
                    //                    sendChatMessageInputRequestModel.setPatientId(strUid);
                    sendChatMessageInputRequestModel.setSenderName(consultMessage.getName())
                    sendChatMessageInputRequestModel.setUrgent(false)
                    sendChatMessageInputRequestModel.setImportant(false)
                    sendChatMessageInputRequestModel.setTime(consultMessage.getTime())

                    // Newly added
                    sendChatMessageInputRequestModel.setId(providerID)
                    sendChatMessageInputRequestModel.setToken(token)
                    sendChatMessageInputRequestModel.setProviderId(providerID)

                    runOnUiThread {
                        chatViewModel.sendChatMessageCall(
                            sendChatMessageInputRequestModel,
                            consultMessage.getId()
                        ).observe(this) { sendChatMessageOutuputResponseModel1 ->
                            if ((sendChatMessageOutuputResponseModel1 != null) && (sendChatMessageOutuputResponseModel1.getStatus() != null) && sendChatMessageOutuputResponseModel1.getStatus()) {

                                lastMessageKey = consultMessage.getId()

                                messageId =
                                    sendChatMessageOutuputResponseModel1.getChatMessages().getId()
                                        .toLong()
                                mFirebaseDatabaseReference!!.child("consults")
                                    .child(strUid.toString())
                                    .child("messages")
                                    .child(sendChatMessageOutuputResponseModel1.getChatId())
                                    .child("messageId").setValue(
                                        sendChatMessageOutuputResponseModel1.getChatMessages()
                                            .getId()
                                            .toLong()
                                    )
                                val messageStatusList: ArrayList<ChatMessageStatusModel> =
                                    ArrayList<ChatMessageStatusModel>(
                                        sendChatMessageOutuputResponseModel1.getChatMessageStatusList()
                                    )
                                for (i in messageStatusList.indices) {
                                    messageStatusList.get(i).time
                                        //.setTime((System.currentTimeMillis()))
                                    mFirebaseDatabaseReference!!.child("consults")
                                        .child(strUid.toString()).child("messages")
                                        .child(sendChatMessageOutuputResponseModel1.getChatId())
                                        .child("ChatMessageStatusList")
                                        .child(
                                            sendChatMessageOutuputResponseModel1.getChatMessageStatusList()
                                                .get(i).getReceiverId()
                                        )
                                        .setValue(messageStatusList.get(i))
                                        .addOnSuccessListener(object :
                                            OnSuccessListener<Void?> {
                                            override fun onSuccess(aVoid: Void?) {
                                                // Write was successful!
                                                // ...
                                                //  Toast.makeText(ChatActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                                sendApiSuccessful = false
                                                dismissProgressBar()
                                            }
                                        })
                                        .addOnFailureListener(object :
                                            OnFailureListener {
                                            override fun onFailure(e: Exception) {
                                                // Write failed
                                                // ...
                                                //    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                sendApiSuccessful = false
                                                dismissProgressBar()
                                            }
                                        })
                                }
                            } else {
                                val errMsg: String? = ErrorMessages().getErrorMessage(
                                    this,
                                    sendChatMessageOutuputResponseModel1.getErrorMessage(),
                                    Constants.API.startCall
                                )
                                CustomSnackBar.make(
                                    chatActBinding?.getRoot(),
                                    this,
                                    CustomSnackBar.WARNING,
                                    errMsg,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0
                                )?.show()
                            }
                        }
                    }

                } catch (e: Exception) {
                }
            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            dismissProgressBar()
        }
    }

   object{
        val DEFAULT_MSG_LENGTH_LIMIT = 10
        val ANONYMOUS = "anonymous"
        private val TAG = "ChatActivity"
        private val REQUEST_INVITE = 1
        private val REQUEST_IMAGE = 2
        private val REQUEST_FILE = 3
        private val REQUEST_VIDEO = 4
        private val REQUEST_AUDIO = 5
        private val REQUEST_CAMERA = 6
        private val FIREBASE_STORAGE_URL = "https://firebasestorage.googleapis.com/v0/b/"
        private val LOADING_IMAGE_URL =
            "https://firebasestorage.googleapis.com/v0/b/omnicure.appspot.com/o/spin-32.gif?alt=media&token=f5a877ef-bf5b-4d54-85da-a30a5d8ce98d" //"https://www.google.com/images/spin-32.gif";
        private val VIDEO_THUMB =
            "https://firebasestorage.googleapis.com/v0/b/omnicure.appspot.com/o/video_thumb.jpg?alt=media&token=2c151d7e-4101-488a-869a-ed25530e57ac"
        private val MESSAGE_SENT_EVENT = "message_sent"
        private val MESSAGE_URL = "http://friendlychat.firebase.google.com/message/"
        var isIsKeyboardShowing = false

        private var keypadHeight = 0
        private var chatEditTextViewHeight = 0
        @JvmName("setIsKeyboardShowing1")
        fun setIsKeyboardShowing(isKeyboardShowing: Boolean) {
            isIsKeyboardShowing = isKeyboardShowing
        }

        fun getKeypadHeight(): Int {
            return keypadHeight
        }

        fun setKeypadHeight(keypadHeight: Int) {
            Companion.keypadHeight = keypadHeight
        }

        fun getChatEditTextViewHeight(): Int {
            return chatEditTextViewHeight
        }

        fun setChatEditTextViewHeight(chatEditTextViewHeight: Int) {
            Companion.chatEditTextViewHeight = chatEditTextViewHeight
        }
    }
}