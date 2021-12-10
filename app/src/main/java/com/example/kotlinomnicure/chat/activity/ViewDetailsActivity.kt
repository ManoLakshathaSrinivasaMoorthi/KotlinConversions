package com.example.kotlinomnicure.chat.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.*
import com.example.kotlinomnicure.chat.adapter.ChatMessageStatusListAdapter
import com.example.kotlinomnicure.databinding.ActivityViewDetialsBinding
import com.example.kotlinomnicure.helper.ClickHelper
import com.example.kotlinomnicure.model.ConsultMessage
import com.example.kotlinomnicure.model.ConsultProvider
import com.example.kotlinomnicure.utils.*
import com.example.kotlinomnicure.viewmodel.ChatActivityViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import omnicurekotlin.example.com.patientsEndpoints.model.PatientHistory
import omnicurekotlin.example.com.patientsEndpoints.model.PatientHistoryResponse
import omnicurekotlin.example.com.providerEndpoints.model.ChatMessageStatusModel
import omnicurekotlin.example.com.providerEndpoints.model.Members
import java.util.ArrayList

class ViewDetailsActivity : BaseActivity() {
    private val membersList: ArrayList<Members>? = ArrayList<Members>()
    var encKey: String? = null
    var strUid = " "
    var messageId = ""
    private val TAG = ViewDetailsActivity::class.java.simpleName
    private var binding: ActivityViewDetialsBinding? = null
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private val viewDetailsReferenceDB: DatabaseReference? = null

    //    private volatile ArrayList<ChatMessageStatusModel> chatMessageList = new ArrayList<ChatMessageStatusModel>();
    private val chatMessageList: ArrayList<ChatMessageStatusModel> =
        ArrayList<ChatMessageStatusModel>()
    private var expand = false
    private var chatViewModel: ChatActivityViewModel? = null
    private var patientHistoryListBackup: List<PatientHistory>? = ArrayList<PatientHistory>()
    private var patientHistoryList: MutableList<PatientHistory?>? = null
    private var patientHistory: PatientHistory? = null
    private var MESSAGES_CHILD: String? = null
    private var mConsultProvider: ConsultProvider? = null
    var callagain = 0
    private var errorTeams: String? = null

    // Adapter
    var chatAdapterSent: ChatMessageStatusListAdapter? = null
    var chatAdapterDeliver: ChatMessageStatusListAdapter? = null
    var chatAdapterRead: ChatMessageStatusListAdapter? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_detials)
        var intentMessage: ConsultMessage? = null
        intentMessage =
            getIntent().getSerializableExtra(Constants.IntentKeyConstants.MESSAGE_OBJECT) as ConsultMessage
        // Get patient History API
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
        EncUtil().generateKey(this)
        //        encKey = EncUtil.decrypt(this, PrefUtility.getAESKey(this));
        encKey = PrefUtility().getAESAPIKey(this)
        strUid = getIntent().getStringExtra(Constants.IntentKeyConstants.STR_UID).toString()
        val patientStatus: Boolean =
            getIntent().getBooleanExtra(Constants.IntentKeyConstants.PATIENT_STATUS, false)
        messageId = intentMessage.getId().toString()
        setMessage(intentMessage)
        val patientName: String? =
            getIntent().getStringExtra(Constants.IntentKeyConstants.FIRST_NAME)
        if (patientName != null) {
            setToolbar(intentMessage, patientName, patientStatus)
        }
        chatViewModel = ViewModelProvider(this).get(ChatActivityViewModel::class.java)
        MESSAGES_CHILD = getIntent().getStringExtra(Constants.IntentKeyConstants.MESSAGE_CHILD)
        mConsultProvider = getIntent().getExtras()
            ?.getSerializable(Constants.IntentKeyConstants.CONSULT_PROVIDER) as ConsultProvider
        getPatientChatHistory(intentMessage)

        // Calling team member API
        showProgressBar()
        // Get data from firebase
        mConsultProvider!!.getPatientsId()?.let { mConsultProvider!!.getTeamName()?.let { it1 ->
            getTeamMemberDetails(it,
                it1
            )
        } }
    }

    private fun setToolbar(intentMessage: ConsultMessage?, patientName: String, status: Boolean) {
        binding?.idToolbarTitle?.setText(patientName)
        binding?.imgBack?.setOnClickListener(View.OnClickListener { onBackPressed() })
        if (status) {
            binding?.imgTeamCall?.setVisibility(View.GONE)
        }
        binding?.imgTeamCall?.setOnClickListener(View.OnClickListener { v ->
                ClickHelper().handleMultipleClick(v)
                //                                            System.out.println("memberlist " + membersList);
                if (membersList!!.size > 0 && membersList != null) {
                    mConsultProvider?.let {
                        showTeamMembersDialog(this@ViewDetailsActivity, membersList,
                            it
                        )
                    }
                } else {
                    CustomSnackBar.make(
                        binding?.getRoot(), this@ViewDetailsActivity,
                        CustomSnackBar.WARNING, "error", CustomSnackBar.TOP, 3000, 0
                    )?.show()
                }
            }
            )
        // View details click listeners
        binding?.rlViewDetails?.setOnClickListener { v ->
            // Expand if the view details drop down is clicked
            expand = if (!expand) {
                binding?.imgViewDetailsArrow?.setImageResource(R.drawable.ic_updown_viewdetails)
                binding?.llExpandViewDetails?.setVisibility(View.VISIBLE)
                true
            } else {
                // Collapse if the view is clicked again
                binding?.imgViewDetailsArrow?.setImageResource(R.drawable.ic_dropdown_viewdetails)
                binding?.llExpandViewDetails?.setVisibility(View.GONE)
                false
            }
        }
        // Intent to View urgent messages activity click listener
        binding?.imgViewUrgentMsgs?.setOnClickListener { v ->
            expand = if (!expand) {
                binding!!.imgViewDetailsArrow.setImageResource(R.drawable.ic_updown_viewdetails)
                binding!!.llExpandViewDetails.setVisibility(View.VISIBLE)
                true
            } else {
                // Collapse if the view is clicked again
                binding!!.imgViewDetailsArrow.setImageResource(R.drawable.ic_dropdown_viewdetails)
                binding!!.llExpandViewDetails.setVisibility(View.GONE)
                false
            }


            // Handling the multi click
           ClickHelper().handleMultipleClick(v)

            // Directing the user to View urgent messages activity with needed data
            val intentConsultChart = Intent(this, ViewUrgentMessagesActivity::class.java)
            intentConsultChart.putExtra("path", "consults/" + intentMessage?.getId())
            var ph: PatientHistory? = patientHistory
            //            System.out.println("patienthistorystr1Urgent " + ph);
            if (patientHistoryListBackup!!.size == 1) {
                ph = patientHistoryListBackup!![0]
            }
            if (ph == null) {
                CustomSnackBar.make(
                    binding!!.getRoot(), this, CustomSnackBar.WARNING,
                    getString(R.string.no_messages_to_filter), CustomSnackBar.TOP, 3000, 0)
                    ?.show()
                return@setOnClickListener
            }
            intentConsultChart.putExtra("inviteTime", ph.getInviteTime())
            intentConsultChart.putExtra("status", ph.getStatus())
            intentConsultChart.putExtra("dischargeTime", ph.getDischargeTime())
            startActivity(intentConsultChart)
        }


        // Intent to View attachements message activity click listener
        binding!!.imgViewAttachments.setOnClickListener { v ->
            expand = if (!expand) {
                binding!!.imgViewDetailsArrow.setImageResource(R.drawable.ic_updown_viewdetails)
                binding!!.llExpandViewDetails.setVisibility(View.VISIBLE)
                true
            } else {
                // Collapse if the view is clicked again
                binding!!.imgViewDetailsArrow.setImageResource(R.drawable.ic_dropdown_viewdetails)
                binding!!.llExpandViewDetails.setVisibility(View.GONE)
                false
            }


            // Handling the multi click
            ClickHelper().handleMultipleClick(v)
            // Intent to View attachments
            val intent = Intent(this, ActivityAttachmentFilter::class.java)
            var ph: PatientHistory? = patientHistory
            if (patientHistoryListBackup!!.size == 1) {
                ph = patientHistoryListBackup!![0]
            }

            if (ph == null) {
                CustomSnackBar.make(
                    binding!!.getRoot(),
                    this,
                    CustomSnackBar.WARNING,
                    getString(R.string.no_messages_to_filter),
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
                return@setOnClickListener
            }
            intent.putExtra("inviteTime", ph?.getInviteTime())
            intent.putExtra("dischargeTime", ph?.getDischargeTime())
            intent.putExtra("status", ph?.getStatus())
            intent.putExtra("messageChild", MESSAGES_CHILD)
            startActivity(intent)
        }
    }

    private fun setPatientChatHistoryView() {
        if (patientHistoryListBackup == null || patientHistoryListBackup!!.isEmpty() || patientHistoryListBackup!!.size == 1) {
            return
        }


        if (patientHistoryList == null) {
            patientHistoryList = ArrayList<PatientHistory?>()
        }
        patientHistoryList!!.clear()
        patientHistory = null
        for (history in patientHistoryListBackup!!) {

            if (history != null && !TextUtils.isEmpty(history.getStatus())
                && history.getStatus()
                    .equals(Constants.PatientStatus.Completed.toString(),ignoreCase = true)
            ) {
                patientHistoryList!!.add(history)

            } else {
                patientHistory = history

            }
        }
    }

    /**
     * Getting the message details/info from firebase database
     * @param strUid
     * @param messageId
     */
    private fun getDataFromFirebase(strUid: String, messageId: String) {

        mFirebaseDatabaseReference?.child("consults")?.child(strUid)?.child("messages")
            ?.child(messageId)?.child("ChatMessageStatusList")?.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        Log.e(TAG, "onDataChange: Eventlist-->"+dataSnapshot );
                        // Get Post object and use the values to update the UI
                        dismissProgressBar()
                        chatMessageList.clear()
                        for (ds2 in dataSnapshot.getChildren()) {
                            if (ds2.exists()) {
                                val messageStatusList: ChatMessageStatusModel? = ds2.getValue(
                                    ChatMessageStatusModel::class.java
                                )
                                if (messageStatusList != null) {
                                    chatMessageList.add(messageStatusList)
                                }
                            }
                        }

                        setRecyclerView()


                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        dismissProgressBar()

                    }
                }
            )
    }


    private fun setRecyclerView() {
        val chatMessageListSent: ArrayList<ChatMessageStatusModel> =
            ArrayList<ChatMessageStatusModel>()
        val chatMessageListDelivered: ArrayList<ChatMessageStatusModel> =
            ArrayList<ChatMessageStatusModel>()
        val chatMessageListRead: ArrayList<ChatMessageStatusModel> =
            ArrayList<ChatMessageStatusModel>()

        for (i in chatMessageList.indices) {
            if (MessageType.sent.toString().equals(
                    chatMessageList[i].status, ignoreCase = true
                )
            ) {
                chatMessageListSent.add(chatMessageList[i])
            } else if (MessageType.read.toString().equals(
                    chatMessageList[i].status, ignoreCase = true
                )
            ) {
                chatMessageListRead.add(chatMessageList[i])
            } else if (MessageType.Received.toString().equals(
                    chatMessageList[i].status, ignoreCase = true
                )
            ) {
                chatMessageListDelivered.add(chatMessageList[i])
            }
        }
        chatAdapterSent = ChatMessageStatusListAdapter(this, chatMessageListSent, false, membersList)
        binding?.recyclerViewSent?.setAdapter(chatAdapterSent)
        chatAdapterDeliver =
            ChatMessageStatusListAdapter(this, chatMessageListDelivered, false, membersList)
        binding?.recyclerViewDelivered?.setAdapter(chatAdapterDeliver)
        chatAdapterRead = ChatMessageStatusListAdapter(this, chatMessageListRead, true, membersList)
        binding?.rvReadBy?.setAdapter(chatAdapterRead)
        if (chatMessageListSent.size == 0) {
            callagain++
            binding?.llsent?.setVisibility(View.GONE)
            binding?.linearsent?.setVisibility(View.GONE)
            binding?.recyclerViewSent?.setVisibility(View.GONE)
        } else {
            binding?.tick?.setImageResource(R.drawable.ic_single_tick)
            binding?.llsent?.setVisibility(View.VISIBLE)
            binding?.linearsent?.setVisibility(View.VISIBLE)
            binding?.recyclerViewSent?.setVisibility(View.VISIBLE)
        }
        if (chatMessageListDelivered.size == 0) {
            callagain++
            binding?.lldeliv?.setVisibility(View.GONE)
            binding?.linearDeliver?.setVisibility(View.GONE)
            binding?.recyclerViewDelivered?.setVisibility(View.GONE)
        } else {
            binding?.tick?.setImageResource(R.drawable.ic_unread)
            binding?.lldeliv?.setVisibility(View.VISIBLE)
            binding?.linearDeliver?.setVisibility(View.VISIBLE)
            binding?.recyclerViewDelivered?.setVisibility(View.VISIBLE)
        }
        if (chatMessageListRead.size == 0) {
            callagain++
            binding?.linearRead?.setVisibility(View.GONE)
            binding?.rvReadBy?.setVisibility(View.GONE)
        } else {
            binding?.tick?.setImageResource(R.drawable.ic_read)
            binding?.linearRead?.setVisibility(View.VISIBLE)
            binding?.rvReadBy?.setVisibility(View.VISIBLE)
        }


    }

    private fun setMessage(intentMessage: ConsultMessage?) {
//        Log.e(TAG, "setMessage: ConsultMessage-->"+new Gson().toJson(intentMessage) );
        val name: String? = intentMessage?.getName()
        val splitName = name?.split(",")?.toTypedArray()
        binding?.messengerName?.setText(splitName?.get(0))
        binding?.messageRole?.setText(splitName?.get(1))
        val type: String? = intentMessage?.getType()
        if (type.equals("text", ignoreCase = true)) {
            val chat: String? = intentMessage?.getText()
            binding?.messengerText?.setText(chat?.let { encKey?.let { it1 ->
                AESUtils().decryptData(it,
                    it1
                )
            } })
        } else if (type.equals("image", ignoreCase = true)) {
            val chat: String? = intentMessage?.getImageUrl()
            val imageUrl: String? = chat?.let { encKey?.let { it1 ->
                AESUtils().decryptData(it,
                    it1
                ) } }
            // todo use this code
            if (imageUrl?.startsWith("gs://") == true) {
                val storageReference: StorageReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(imageUrl)
                storageReference.getDownloadUrl().addOnCompleteListener(
                    OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result.toString()
                            Glide.with(binding?.messageImageViewRight?.getContext())
                                .load(downloadUrl)
                                .into(binding?.messageImageViewRight)
                        } else {
//
                        }
                    })
            } else {
                if (binding?.messageImageViewRight?.getTag() == null) {
                    Glide.with(binding?.messageImageViewRight?.getContext())
                        .load(imageUrl)
                        .into(binding?.messageImageViewRight)
                } else {
                    binding?.messageImageViewRight!!.setTag(null)
                    Glide.with(binding?.messageImageViewRight!!.getContext())
                        .load(imageUrl)

                        .into(binding!!.messageImageViewRight)
                }
            }
            binding?.messageImageViewRight?.setVisibility(ImageView.VISIBLE)
            binding?.messengerText?.setVisibility(ImageView.GONE)
            binding?.imageViewRightLayout?.setVisibility(ImageView.VISIBLE)
        } else if (MessageType.video.toString().equals(type, ignoreCase = true)) {
            val chat: String? = intentMessage?.getImageUrl()
            val imageUrl: String? = chat?.let { encKey?.let { it1 ->
                AESUtils().decryptData(it,
                    it1
                )
            } }
            if (intentMessage?.getThumbUrl() == null) {

                binding?.imagePBRight?.setVisibility(View.VISIBLE)
                binding?.imgPlayIconRight?.setVisibility(View.GONE)
                binding?.imageViewRightLayout?.setEnabled(false)
                Glide.with(binding?.messageImageViewRight?.getContext())
                    .load(imageUrl)
                    .listener(object : RequestListener<Drawable?> {



                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean): Boolean {
                            binding?.imagePBRight?.setVisibility(View.GONE)
                            binding?.imageViewRightLayout?.setEnabled(true)
                            binding?.imgPlayIconRight?.setVisibility(View.VISIBLE)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding?.imagePBRight?.setVisibility(View.GONE)
                            binding?.imageViewRightLayout?.setEnabled(true)
                            binding?.imgPlayIconRight?.setVisibility(View.VISIBLE)
                            return false
                        }
                    })
                    .into(binding?.messageImageViewRight)
            } else {

                binding?.imageViewRightLayout?.setVisibility(View.VISIBLE)
                binding?.messageImageViewRight?.setVisibility(View.VISIBLE)
                Glide.with(binding?.messageImageViewRight?.getContext())
                    //.load(imageUrl).
                   // addListener(object : RequestListener<Drawable?> {

                      /*  override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean): Boolean {
                            binding?.imagePBRight?.setVisibility(View.GONE)
                            binding?.imageViewRightLayout?.setEnabled(false)
                            binding?.imgPlayIconRight?.setVisibility(View.GONE)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding?.imagePBRight?.setVisibility(View.GONE)
                            binding?.imageViewRightLayout?.setVisibility(View.VISIBLE)
                            binding?.imageViewRightLayout?.setEnabled(true)
                            binding?.imgPlayIconRight?.setVisibility(View.VISIBLE)
                            binding?.messageImageViewRight?.setImageDrawable(resource)
                            binding?.messageImageViewRight?.setVisibility(View.VISIBLE)
                            return false
                        }
                    }).into(binding?.messageImageViewRight)*/


                binding?.messengerText?.setVisibility(View.GONE)

                binding?.imageViewRightLayout?.setOnClickListener { v ->
                    val intent = Intent(this@ViewDetailsActivity, VideoPlayerActivity::class.java)
                    intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, imageUrl)
                    startActivity(intent)
                }
            }
        } else if (MessageType.file.toString().equals(type, ignoreCase = true)) {
            val fileUrl: String? = intentMessage?.getImageUrl()
            val imageUrl: String? = fileUrl?.let { encKey?.let { it1 ->
                AESUtils().decryptData(it,
                    it1)
            } }
            if (!imageUrl?.startsWith(LOADING_IMAGE_URL)!!) {
                binding?.fileIconRight?.setImageResource(getFileIcon(intentMessage))
                binding?.fileTextViewRight?.setText(intentMessage.getFilename())
                binding?.fileViewRight?.setVisibility(View.VISIBLE)
                binding?.messageViewRight?.setVisibility(View.VISIBLE)
                binding?.framefileViewRight?.setVisibility(View.VISIBLE)
                binding?.messengerText?.setVisibility(View.GONE)
            } else {
                Glide.with(binding?.messageImageViewRight?.getContext())
                    .load(imageUrl)
                    .into(binding?.messageImageViewRight)
                binding?.messageImageViewRight?.setVisibility(View.VISIBLE)
                binding?.imageViewRightLayout?.setVisibility(View.VISIBLE)
                binding?.messengerText?.setVisibility(View.GONE)
            }
            binding?.messageViewRight?.setOnClickListener { v ->
                val intent = Intent(this@ViewDetailsActivity, PDFViewerActivity::class.java)
                intent.putExtra(Constants.IntentKeyConstants.PDF_URL, imageUrl)
                startActivity(intent)
            }
        }
        val time: Long? = intentMessage?.getTime()
        val timetoday = System.currentTimeMillis()

        binding?.tvDateMessage?.setText(time?.let { ChatUtils().getDateFormatViewDetail(it) })
        binding?.date?.setText(time?.let { ChatUtils().getStatusDateFormat(it) })
        if ("Read".equals(intentMessage?.getStatus(), ignoreCase = true)) {
            binding?.tick?.setImageResource(R.drawable.ic_read)
        }
        if ("Received".equals(intentMessage?.getStatus(), ignoreCase = true)) {
            binding?.tick?.setImageResource(R.drawable.ic_unread)
        } else {
            binding?.tick?.setImageResource(R.drawable.ic_single_tick)
        }
    }

    private fun getPatientChatHistory(intentMessage: ConsultMessage?) {
        if (!UtilityMethods().isInternetConnected(this)!!) {

            CustomSnackBar.make(
                binding?.getRoot(),
                this,
                CustomSnackBar.WARNING,
                getString(R.string.patient_history_error_due_to_internet),
                CustomSnackBar.TOP,
                3000,
                0
            )?.show()
            return
        }
        //showProgressBar(getString(R.string.fetching_patient_history_pb_msg));
        val providerId: Long? = PrefUtility().getProviderId(this)
        val token: String? = PrefUtility().getToken(this)
        val patientId: Long? = intentMessage?.getMessageId()
        chatViewModel?.getPatientChatHistory(providerId, token, patientId)
            ?.observe(this, object : Observer<PatientHistoryResponse?> {
                override fun onChanged(response: PatientHistoryResponse?) {
                    dismissProgressBar()
                    if (response != null && response.getStatus() != null && response.getStatus()!!) {
                        if (response.getPatientHistoryList() != null) {
                            patientHistoryListBackup = response.getPatientHistoryList() as List<PatientHistory>?

                            setPatientChatHistoryView()
                        }
                    } else {
                        var errMsg: String? = null
                        if (response != null) {
                            errMsg = ErrorMessages().getErrorMessage(
                                this,
                                response.getErrorMessage(),
                                Constants.API.getPatientHistory
                            )
                        }

                        CustomSnackBar.make(
                            binding?.getRoot(),
                            this@ViewDetailsActivity,
                            CustomSnackBar.WARNING,
                            errMsg,
                            CustomSnackBar.TOP,
                            3000,
                            0
                        )?.show()
                    }
                }
            })
    }

    /**
     * Get team member's list details api call
     * @param patientId
     * @param teamName
     */
    private fun getTeamMemberDetails(patientId: Long, teamName: String) {
        membersList!!.clear()

        chatViewModel?.getMemberList(patientId, teamName)?.observe(this) { response ->
            dismissProgressBar()
            if (response?.getStatus() != null && response.getStatus()!!) {

                if (response.getTeamDetails()?.getMembers() != null) {
                   // membersList.addAll(response.teamDetails!!.getMembers())
                    //Getting the message details/info from firebase database
                    getDataFromFirebase(strUid, messageId)
                }
            } else if (!TextUtils.isEmpty(response?.getErrorMessage()) && response?.getErrorMessage() != null) {
                dismissProgressBar()
                errorTeams = response.getErrorMessage()
                //                Log.d(TAG, "GetTeamMemberDetails errorTeams :  " + errorTeams);
            } else {
                dismissProgressBar()
                errorTeams = getString(R.string.api_error)
            }
        }
    }

    private enum class MessageType {
        system, text, Discharged, Exit, image, file, video, sent, Received, read
    }

    private fun getFileIcon(consultMessage: ConsultMessage?): Int {
        if (consultMessage == null || TextUtils.isEmpty(consultMessage.getFilename())) {
            return R.drawable.ic_generic
        }
        val fileName: String? = consultMessage.getFilename()?.toLowerCase()
        return if (fileName?.contains(".txt") == true) {
            R.drawable.ic_txt
        } else if (fileName?.contains(".doc") == true || fileName?.contains(".docx") == true) {
            R.drawable.ic_doc
        } else if (fileName?.contains(".pdf") == true) {
            R.drawable.ic_pdf
        } else if (fileName?.contains(".xls") == true || fileName?.contains(".xlsx") == true) {
            R.drawable.ic_xls
        } else if (fileName?.contains(".ppt") == true || fileName?.contains(".pptx") == true) {
            R.drawable.ic_ppt
        } else {
            R.drawable.ic_generic
        }
    }

    companion object {
        private const val LOADING_IMAGE_URL =
            "https://firebasestorage.googleapis.com/v0/b/omnicure.appspot.com/o/spin-32.gif?alt=media&token=f5a877ef-bf5b-4d54-85da-a30a5d8ce98d"
    }
}