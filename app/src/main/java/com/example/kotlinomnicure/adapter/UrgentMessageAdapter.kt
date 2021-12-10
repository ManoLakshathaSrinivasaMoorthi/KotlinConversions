package com.example.kotlinomnicure.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.kotlinomnicure.viewmodel.ChatActivityViewModel
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.PDFViewerActivity
import com.example.kotlinomnicure.activity.VideoPlayerActivity
import com.example.kotlinomnicure.activity.ViewUrgentMessagesActivity
import com.example.kotlinomnicure.activity.WebViewActivity
import com.example.kotlinomnicure.databinding.ItemUrgentMessagesBinding
import com.example.kotlinomnicure.model.ConsultMessage
import com.example.kotlinomnicure.utils.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import omnicurekotlin.example.com.providerEndpoints.model.ChatMessageStatusModel
import omnicurekotlin.example.com.providerEndpoints.model.SendChatMessageInputRequestModel
import java.lang.Exception
import java.util.ArrayList


class UrgentMessageAdapter(
    context: ViewUrgentMessagesActivity,
    list: ArrayList<ConsultMessage?>,
    key: String,
    sid: String,
    strUID: String,
) :
    RecyclerView.Adapter<UrgentMessageAdapter.ViewHolder>() {
    private val uID: String
    var context: ViewUrgentMessagesActivity
    var listen: AttachmentListener? = null
    var encKey: String
    var senderId: String
    private var messageList: ArrayList<ConsultMessage> = ArrayList<ConsultMessage>()
    private var popupWindow: PopupWindow? = null
    private val messageId: Long = 0
    private var mFirebaseDatabaseReference: DatabaseReference? = null
    private var chatViewModel: ChatActivityViewModel? = null

    internal enum class MessageTypeRead {
        system, text, Discharged, Exit, image, file, video, sent, received, read
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrgentMessageAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: ItemUrgentMessagesBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_urgent_messages, parent, false)
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        chatViewModel = ViewModelProvider(context).get(ChatActivityViewModel::class.java)
        return UrgentMessageAdapter.ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: UrgentMessageAdapter.ViewHolder,
        position: Int,
    ) {

//        Log.i(TAG, "onBindViewHolder: MESSAGELIST " + new Gson().toJson(messageList));
        handleChatMessage(messageList[position], holder.binding, position, holder)
    }

    private fun handleChatMessage(
        consultMessage: ConsultMessage,
        binding: ItemUrgentMessagesBinding,
        position: Int,
        holder: UrgentMessageAdapter.ViewHolder,
    ) {
        // If the sender id matches - self mark as urgent messages needs to be displyed
        if (senderId.equals(consultMessage.getSenderId(), ignoreCase = true)) {
            // Profile pic loading
            val imageURL: String? = PrefUtility().getStringInPref(context,
                Constants.SharedPrefConstants.PROFILE_IMG_URL, "")



            //If type is text
            if (consultMessage.getType().equals(UrgentMessageAdapter.MessageType.text.toString(),ignoreCase = true)) {
                binding.cardViewDescSender.setCardBackgroundColor(context.getResources()
                    .getColor(R.color.bg_gray))

                // Decrypted and displayed to the user
                val res: List<String> = consultMessage.getName()!!.split(",")
                binding.tvUrgentFullName.setText(res[0])
                binding.tvProviderType.setText(res[1])
                binding.descriptionSender.setText(consultMessage.getText()
                    ?.trim()?.let { AESUtils().decryptData(it, encKey) })

                binding.descriptionSender.setTextColor(context.getResources()
                    .getColor(R.color.colorPrimary))
                binding.cardViewDescSender.setVisibility(View.VISIBLE)
                binding.descriptionSender.setVisibility(View.VISIBLE)
                binding.cardViewImgUrgent.setVisibility(View.GONE)
                binding.cardViewVideoUrgent.setVisibility(View.GONE)
                binding.cardViewFileUrgent.setVisibility(View.GONE)
            } else {

                // For other media types
                if (consultMessage.getType()
                        .equals(UrgentMessageAdapter.MessageType.image.toString(),ignoreCase = true)
                ) {
                    val imageUrl: String? =
                        consultMessage.getImageUrl()?.let { AESUtils().decryptData(it, encKey) }

                    if (imageUrl?.startsWith("gs://") == true) {
                        val storageReference = FirebaseStorage.getInstance()
                            .getReferenceFromUrl(imageUrl)
                        storageReference.downloadUrl.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUrl = task.result.toString()
                                Glide.with(context)
                                    .load(downloadUrl)
                                    .into(binding.imgUrgent)
                            } else {

                            }
                        }
                    } else {
                        Glide.with(context)
                            .load(imageUrl)
                            .into(binding.imgUrgent)
                    }
                    binding.cardViewImgUrgent.setVisibility(View.VISIBLE)
                    binding.cardViewImgUrgent.setCardBackgroundColor(context.getResources()
                        .getColor(R.color.bg_gray))
                    //Hiding other file types UI
                    binding.descriptionSender.setVisibility(View.GONE)
                    binding.cardViewDescSender.setVisibility(View.GONE)
                    binding.cardViewVideoUrgent.setVisibility(View.GONE)
                    binding.cardViewFileUrgent.setVisibility(View.GONE)
                } else if (consultMessage.getType()
                        .equals(UrgentMessageAdapter.MessageType.video.toString(),ignoreCase = true)
                ) {
                    val imageUrl: String? =

                        consultMessage.getImageUrl()?.let { AESUtils().decryptData(it, encKey) }


                    // For video view type
                    if (consultMessage.getThumbUrl() == null) {
                        binding.imagePBLeft.setVisibility(View.VISIBLE)
                        binding.imgPlayIconLeft.setVisibility(View.GONE)
                        binding.frameVideoLayout.setEnabled(false)
                        Glide.with(binding.messageImageViewLeft.getContext())
                            .load(imageUrl)
                            .listener(object : RequestListener<Drawable?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any,
                                    target: Target<Drawable?>,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    binding.imagePBLeft.setVisibility(View.GONE)
                                    binding.frameVideoLayout.setEnabled(true)
                                    binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any,
                                    target: Target<Drawable?>,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    binding.imagePBLeft.setVisibility(View.GONE)
                                    binding.frameVideoLayout.setEnabled(true)
                                    binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                    return false
                                }
                            })
                            .into(binding.messageImageViewLeft)
                    } else if (consultMessage.getThumbUrl() != null) {

                        binding.imagePBLeft.setVisibility(View.VISIBLE)
                        binding.imgPlayIconLeft.setVisibility(View.GONE)
                        binding.frameVideoLayout.setEnabled(false)
                        Glide.with(binding.messageImageViewLeft.getContext())
                            .load(imageUrl)
                            .listener(object : RequestListener<Drawable?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any,
                                    target: Target<Drawable?>,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    binding.imagePBLeft.setVisibility(View.GONE)
                                    binding.frameVideoLayout.setEnabled(true)
                                    binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any,
                                    target: Target<Drawable?>,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    binding.imagePBLeft.setVisibility(View.GONE)
                                    binding.frameVideoLayout.setEnabled(true)
                                    binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                    return false
                                }
                            })
                            .into(binding.messageImageViewLeft)
                    }
                    binding.cardViewVideoUrgent.setVisibility(View.VISIBLE)
                    binding.cardViewVideoUrgent.setBackgroundColor(context.getResources()
                        .getColor(R.color.bg_gray))
                    //Hiding other file types UI
                    binding.descriptionSender.setVisibility(View.GONE)
                    binding.cardViewDescSender.setVisibility(View.GONE)
                    binding.cardViewImgUrgent.setVisibility(View.GONE)
                    binding.cardViewFileUrgent.setVisibility(View.GONE)
                } else if (consultMessage.getType()
                        .equals(UrgentMessageAdapter.MessageType.file.toString(),ignoreCase = true)
                ) {
                    val imageUrl: String? =
                        consultMessage.getImageUrl()?.let { AESUtils().decryptData(it, encKey) }


                    // For file view type
                    if (!imageUrl?.startsWith(LOADING_IMAGE_URL)!!) {
                        binding.fileIconLeft.setImageResource(getFileIcon(consultMessage))
                        binding.fileTextViewLeft.setText(consultMessage.getFilename())
                        binding.fileTextViewLeft.setTextColor(context.getResources()
                            .getColor(R.color.colorPrimary))
                        binding.fileViewLeft.setVisibility(ImageView.VISIBLE)
                        binding.frameFileLayout.setVisibility(TextView.VISIBLE)
                    } else {
                        Glide.with(binding.messageImageViewLeft.getContext())
                            .load(imageUrl)
                            .into(binding.imgUrgent)
                        binding.messageTextViewLeft.setVisibility(TextView.GONE)
                        binding.fileViewLeft.setVisibility(ImageView.GONE)
                        binding.frameFileLayout.setVisibility(View.GONE)
                    }
                    binding.cardViewFileUrgent.setVisibility(View.VISIBLE)
                    binding.cardViewFileUrgent.setBackgroundColor(context.getResources()
                        .getColor(R.color.bg_gray))
                    //Hiding other file types UI
                    binding.descriptionSender.setVisibility(View.GONE)
                    binding.cardViewDescSender.setVisibility(View.GONE)
                    binding.cardViewImgUrgent.setVisibility(View.GONE)
                    binding.cardViewVideoUrgent.setVisibility(View.GONE)
                }
            }
        } else {

            // Receiver data

            // User fname & lname loading if image URL is empty
            val messengerName: String? = consultMessage.getName()


            // Text type
            if (consultMessage.getType() != null) {
                if (consultMessage.getType()
                        .equals(UrgentMessageAdapter.MessageType.text.toString(),ignoreCase = true)
                ) {
                    binding.cardViewDescSender.setCardBackgroundColor(context.getResources()
                        .getColor(R.color.colorPrimary))
                    // Decrypted and displayed to the user
                    val res: List<String> = consultMessage.getName()!!.split(",")
                    binding.tvUrgentFullName.setText(res[0])
                    binding.tvProviderType.setText(res[1])
                    binding.descriptionSender.setText(AESUtils().decryptData(consultMessage.getText()!!
                        .trim(), encKey))

                    binding.descriptionSender.setTextColor(context.getResources()
                        .getColor(R.color.bg_gray))
                    binding.cardViewDescSender.setVisibility(View.VISIBLE)
                    binding.descriptionSender.setVisibility(View.VISIBLE)
                    //Hiding other file types UI
                    binding.cardViewImgUrgent.setVisibility(View.GONE)
                    binding.cardViewVideoUrgent.setVisibility(View.GONE)
                    binding.cardViewFileUrgent.setVisibility(View.GONE)
                } else {

                    // For image view type
                    if (consultMessage.getType()
                            .equals(UrgentMessageAdapter.MessageType.image.toString(),ignoreCase = true)
                    ) {
                        // Getting the image url
                        val imageUrl: String? =
                            AESUtils().decryptData(consultMessage.getImageUrl()!!, encKey)

                        if (imageUrl?.startsWith("gs://") == true) {
                            val storageReference = imageUrl?.let {
                                FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(it)
                            }
                            storageReference.downloadUrl.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadUrl = task.result.toString()
                                    Glide.with(context)
                                        .load(downloadUrl)
                                        .into(
                                            binding.imgUrgent)
                                } else {
                                    //                                                Log.w(TAG, "Getting download url was not successful.",
                                    //                                                        task.getException());
                                }
                            }
                        } else {
                            Glide.with(context)
                                .load(imageUrl)
                                .into(binding.imgUrgent)
                        }
                        binding.cardViewImgUrgent.setVisibility(View.VISIBLE)
                        binding.cardViewImgUrgent.setCardBackgroundColor(context.getResources()
                            .getColor(R.color.colorPrimary))
                        //Hiding other file types UI
                        binding.descriptionSender.setVisibility(View.GONE)
                        binding.cardViewDescSender.setVisibility(View.GONE)
                        binding.cardViewVideoUrgent.setVisibility(View.GONE)
                        binding.cardViewFileUrgent.setVisibility(View.GONE)
                    } else if (consultMessage.getType()
                            .equals(UrgentMessageAdapter.MessageType.video.toString())
                    ) {
                        //For video type
                        // Getting the image url
                        val imageUrl: String?=
                            AESUtils().decryptData(consultMessage.getImageUrl()!!, encKey)

                        if (consultMessage.getThumbUrl() == null) {

                            binding.imagePBLeft.setVisibility(View.VISIBLE)
                            binding.imgPlayIconLeft.setVisibility(View.GONE)
                            binding.frameVideoLayout.setEnabled(false)
                            Glide.with(binding.messageImageViewLeft.getContext())
                                .load(imageUrl)
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        isFirstResource: Boolean,
                                    ): Boolean {
                                        binding.imagePBLeft.setVisibility(View.GONE)
                                        binding.frameVideoLayout.setEnabled(true)
                                        binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean,
                                    ): Boolean {
                                        binding.imagePBLeft.setVisibility(View.GONE)
                                        binding.frameVideoLayout.setEnabled(true)
                                        binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                        return false
                                    }
                                })
                                .into(binding.messageImageViewLeft)
                        } else if (consultMessage.getThumbUrl() != null) {

                            binding.imagePBLeft.setVisibility(View.VISIBLE)
                            binding.imgPlayIconLeft.setVisibility(View.GONE)
                            binding.frameVideoLayout.setEnabled(false)
                            Glide.with(binding.messageImageViewLeft.getContext())
                                .load(imageUrl)
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        isFirstResource: Boolean,
                                    ): Boolean {
                                        binding.imagePBLeft.setVisibility(View.GONE)
                                        binding.frameVideoLayout.setEnabled(true)
                                        binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any,
                                        target: Target<Drawable?>,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean,
                                    ): Boolean {
                                        binding.imagePBLeft.setVisibility(View.GONE)
                                        binding.frameVideoLayout.setEnabled(true)
                                        binding.imgPlayIconLeft.setVisibility(View.VISIBLE)
                                        return false
                                    }
                                })
                                .into(binding.messageImageViewLeft)
                        }
                        binding.cardViewVideoUrgent.setVisibility(View.VISIBLE)
                        binding.cardViewVideoUrgent.setBackgroundColor(context.getResources()
                            .getColor(R.color.colorPrimary))
                        //Hiding other file types UI
                        binding.descriptionSender.setVisibility(View.GONE)
                        binding.cardViewDescSender.setVisibility(View.GONE)
                        binding.cardViewImgUrgent.setVisibility(View.GONE)
                        binding.cardViewFileUrgent.setVisibility(View.GONE)
                    } else if (consultMessage.getType()
                            .equals(UrgentMessageAdapter.MessageType.file.toString(),ignoreCase = true)
                    ) {
                        // For file type
                        // Getting the image url
                        val imageUrl: String? =
                            AESUtils().decryptData(consultMessage.getImageUrl()!!, encKey)

                        if (!imageUrl?.startsWith(LOADING_IMAGE_URL)!!) {
                            binding.fileIconLeft.setImageResource(getFileIcon(consultMessage))
                            binding.fileTextViewLeft.setText(consultMessage.getFilename())
                            binding.fileTextViewLeft.setTextColor(context.getResources()
                                .getColor(R.color.bg_gray))
                            binding.fileViewLeft.setVisibility(ImageView.VISIBLE)
                            binding.frameFileLayout.setVisibility(TextView.VISIBLE)
                        } else {
                            Glide.with(binding.messageImageViewLeft.getContext())
                                .load(imageUrl)
                                .into(binding.imgUrgent)
                            binding.messageTextViewLeft.setVisibility(TextView.GONE)
                            binding.fileViewLeft.setVisibility(ImageView.GONE)
                            binding.frameFileLayout.setVisibility(View.GONE)
                        }
                        binding.cardViewFileUrgent.setVisibility(View.VISIBLE)
                        binding.cardViewFileUrgent.setCardBackgroundColor(context.getResources()
                            .getColor(R.color.colorPrimary))
                        //Hiding other file types UI
                        binding.cardViewDescSender.setVisibility(View.GONE)
                        binding.descriptionSender.setVisibility(View.GONE)
                        binding.cardViewImgUrgent.setVisibility(View.GONE)
                        binding.cardViewVideoUrgent.setVisibility(View.GONE)
                    }
                }
            }
        }

        //Common listings


        // Role and Name display
        val messengerName: String? = consultMessage.getName()
        if (consultMessage.getRole() != null && messengerName != null) {
            val res: List<String> = consultMessage.getName()!!.split(",")
            if (res.size == 2) {
                binding.tvUrgentFullName.setText(res[0])
            } else {
                binding.tvUrgentFullName.setText(consultMessage.getName())
            }
            if (consultMessage.getRole().equals(Constants.ProviderRole.RD.toString(),ignoreCase = true)) {
                if (res.size == 2) {
                    binding.tvProviderType.setText(res[1])
                } else {
                    binding.tvProviderType.setText(context.getResources()
                        .getString(R.string.remote_provider))
                }
            } else if (consultMessage.getRole()
                    .equals(Constants.ProviderRole.BD.toString(),ignoreCase = true)
            ) {
                binding.tvProviderType.setText(context.getResources()
                    .getString(R.string.bedside_provider))
            }
        }


        // Time text view displaying
        binding.timeTextViewRight.setText(consultMessage.getTime()?.let {
            ChatUtils().getStatusDateFormat(it)
        })


        //Send/Read status tick
        if (consultMessage.getStatus() == null) {
            consultMessage.setStatus("Sent")
        }
        consultMessage.getId()?.let { setMessageReadStatus(it, binding.statusTextViewRight) }



        // Set urgent message UI
        binding.imgMarkAsUrgentViewRight.setImageResource(R.drawable.mark_as_urgent_red)
        binding.imgMarkAsUrgentViewRight.setVisibility(View.VISIBLE)
        binding.cardRootView.setOnClickListener { v ->

            //Save the move to msg  click id in shared preference
            PrefUtility().saveStringInPref(context,
                Constants.SharedPrefConstants.MOVE_TO_CHAT_ID,
                consultMessage.getId())
            (context as Activity).finish()
        }
        binding.cardRootView.setOnLongClickListener { view ->

            if (senderId.equals(consultMessage.getSenderId(), ignoreCase = true)) {
                showMarkUnMarkAsUrgentPopup(context,
                    view,
                    binding,
                    consultMessage,
                    false,
                    position,
                    holder)
            }
            false
        }

        //  File click listener
        binding.frameFileLayout.setOnClickListener { v ->
            val imageUrl: String =
                consultMessage.getImageUrl()?.let { AESUtils().decryptData(it, encKey) }!!

            if (UrgentMessageAdapter.MessageType.file.toString()
                    .equals(consultMessage.getType(), ignoreCase = true)
            ) {
                val intent = Intent(context, PDFViewerActivity::class.java)
                intent.putExtra(Constants.IntentKeyConstants.PDF_URL, imageUrl)
                context.startActivity(intent)
            }
        }

        // Video click listener
        binding.frameVideoLayout.setOnClickListener { v ->
            val imageUrl: String? =
                consultMessage.getImageUrl()?.let { AESUtils().decryptData(it, encKey) }

            if (UrgentMessageAdapter.MessageType.video.toString()
                    .equals(consultMessage.getType(), ignoreCase = true)
            ) {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, imageUrl)
                context.startActivity(intent)
            }
        }

        // Image on click listener
        binding.imgUrgent.setOnClickListener { v ->
            val imageUrl: String? =
                consultMessage.getImageUrl()?.let { AESUtils().decryptData(it, encKey) }

            if (UrgentMessageAdapter.MessageType.image.toString()
                    .equals(consultMessage.getType(), ignoreCase = true)
            ) {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra(Constants.IntentKeyConstants.IMAGE_URL, imageUrl)
                context.startActivity(intent)
            }
        }
    }

    private fun setMessageReadStatus(longMessageId: String, statusTextViewRight: ImageView) {
        val chatMessageListStatus = ArrayList<String>()
        //        Log.e("message_path ", "consults/" + strUid + "/messages/" + longMessageId + "/ChatMessageStatusList");
        mFirebaseDatabaseReference!!.child("consults").child(uID).child("messages")
            .child(longMessageId).child("ChatMessageStatusList").addValueEventListener(
                object : ValueEventListener {
                    var returnImage: Int = R.drawable.ic_single_tick
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Get Post object and use the values to update the UI
                        chatMessageListStatus.clear()
                        for (ds2 in dataSnapshot.children) {
                            if (ds2.exists()) {
                                val messageStatusList: ChatMessageStatusModel? = ds2.getValue(
                                    ChatMessageStatusModel::class.java)
                                if (messageStatusList != null) {
                                    messageStatusList.status
                                        ?.toLowerCase()?.let { chatMessageListStatus.add(it) }
                                }
                            }
                        }

                        //Log.e("full response", ": " + new Gson().toJson(chatMessageListStatus));
                        if (!chatMessageListStatus.isEmpty()) {
                            if (chatMessageListStatus.contains(MessageTypeRead.sent.toString())) {
                                returnImage = R.drawable.ic_single_tick
                            } else if (!chatMessageListStatus.contains(MessageTypeRead.sent.toString())
                                && chatMessageListStatus.contains(MessageTypeRead.received.toString())
                            ) {
                                returnImage = R.drawable.ic_unread
                            } else if (!chatMessageListStatus.contains(MessageTypeRead.sent.toString())
                                && !chatMessageListStatus.contains(MessageTypeRead.received.toString())
                                && chatMessageListStatus.contains(MessageTypeRead.read.toString())
                            ) {
                                returnImage = R.drawable.ic_read
                            }
                        }
                        statusTextViewRight.setImageResource(returnImage)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message

                    }
                }
            )
    }

    private fun showMarkUnMarkAsUrgentPopup(
        context: ViewUrgentMessagesActivity,
        v: View,
        binding: ItemUrgentMessagesBinding,
        consultMessage: ConsultMessage,
        lastItem: Boolean,
        position: Int,
        holder: UrgentMessageAdapter.ViewHolder,
    ) {
        val llMarkAsUrgent: LinearLayout
        val llViewDetails: LinearLayout
        val img_mark_as_urgent: ImageView
        val txtMarkAsUrgent: TextView
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = layoutInflater.inflate(R.layout.chat_popup_layout, null)
        llMarkAsUrgent = layout.findViewById<View>(R.id.ll_mark_as_urgent) as LinearLayout
        llViewDetails = layout.findViewById<View>(R.id.ll_view_details) as LinearLayout
        img_mark_as_urgent = layout.findViewById<View>(R.id.img_mark_as_urgent) as ImageView
        txtMarkAsUrgent = layout.findViewById<View>(R.id.txtMarkAsUrgent) as TextView
        popupWindow = PopupWindow(context)
        popupWindow!!.contentView = layout
        popupWindow!!.height = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        popupWindow!!.width = v.measuredWidth - 200
        popupWindow!!.isFocusable = true

//        Log.e(TAG, "showMarkAsurgentPopup: v.getMeasuredHeight()-" + v.getMeasuredHeight());
        popupWindow!!.setBackgroundDrawable(null)
        if (lastItem) {
            if (v.measuredHeight > 300) {
                popupWindow!!.showAsDropDown(v, 0, -(v.measuredHeight + 60), Gravity.NO_GRAVITY)
            } else {
                popupWindow!!.showAsDropDown(v, 0, -(v.measuredHeight + 30), Gravity.NO_GRAVITY)
            }
        } else {
            popupWindow!!.showAsDropDown(v, 0, 30, Gravity.NO_GRAVITY)
        }
        val container = popupWindow!!.contentView.rootView
        val context1 = popupWindow!!.contentView.context
        val wm = context1.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)
        if (consultMessage.isUrgent()) {
            img_mark_as_urgent.setImageResource(R.drawable.ic_star_outline_24)
            txtMarkAsUrgent.setText(context.getResources().getString(R.string.unmark_as_urgent))
        } else {
            img_mark_as_urgent.setImageResource(R.drawable.mark_as_urgent_red)
            txtMarkAsUrgent.setText(context.getResources().getString(R.string.mark_as_urgent))
        }
        llMarkAsUrgent.setOnClickListener { v1: View? ->
            var urgent = true
            urgent = !consultMessage.isUrgent()
            //            if (popupWindow.isShowing()) {
//                popupWindow.dismiss();
//            }
            context.showProgressBar()
            sendMarkAsUrgentAPICall(consultMessage, binding, urgent, position, holder)
        }
        llViewDetails.visibility = View.GONE
        llViewDetails.setOnClickListener { v1: View? -> }
    }

    private fun sendMarkAsUrgentAPICall(
        consultMessage: ConsultMessage,
        binding: ItemUrgentMessagesBinding,
        urgent: Boolean,
        position: Int,
        holder: UrgentMessageAdapter.ViewHolder,
    ) {

//        AsyncTask.execute(() -> {
        // New sendchatMessage API call input request object
        try {
            if (!UtilityMethods().isInternetConnected(context)!!) {

                CustomSnackBar.make(binding.getRoot(),
                    context,
                    CustomSnackBar.WARNING,
                    context.getString(R.string.no_internet_connectivity),
                    CustomSnackBar.TOP,
                    3000,
                    0)?.show()
                return
            }
            var type = ""
            var subType = ""
            if (consultMessage.getType().equals("system",ignoreCase = true)) {
                type = "system"
                subType = "system"
            } else if (consultMessage.getType().equals("text",ignoreCase = true)) {
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


//            Log.e(TAG, "sendMarkAsUrgentAPICall: consultMessage->" + new Gson().toJson(consultMessage));
            // Provider Id
            val providerID: Long =
                PrefUtility().getLongInPref(context, Constants.SharedPrefConstants.USER_ID, -1)

            // Fetching token
            val token: String? =
                PrefUtility().getStringInPref(context, Constants.SharedPrefConstants.TOKEN, "")

            // Input request object
            val sendChatMessageInputRequestModel = SendChatMessageInputRequestModel()

            if (consultMessage.getMessageId()?.equals(0) == true) {
                consultMessage.setMessageId(messageId)
            }
            sendChatMessageInputRequestModel.setId(consultMessage.getMessageId())
            sendChatMessageInputRequestModel.setUrgent(urgent)
            sendChatMessageInputRequestModel.setSenderId(providerID.toString().toInt())
            if (type.equals("Text", ignoreCase = true)) {
                sendChatMessageInputRequestModel.setMessage(consultMessage.getText())
                sendChatMessageInputRequestModel.setTitle("Yes")
            } else {
                sendChatMessageInputRequestModel.setMessage(consultMessage.getImageUrl())
            }
            sendChatMessageInputRequestModel.setType(type)
            sendChatMessageInputRequestModel.setSubType(subType)
            //                sendChatMessageInputRequestModel.setPatientId(consultMessage.getPatientsId());
            sendChatMessageInputRequestModel.setPatientId(java.lang.Long.valueOf(uID))
            //                    sendChatMessageInputRequestModel.setPatientId(strUid);
            sendChatMessageInputRequestModel.setSenderName(consultMessage.getName())
            sendChatMessageInputRequestModel.setUrgent(urgent)
            sendChatMessageInputRequestModel.setImportant(false)
            sendChatMessageInputRequestModel.setTime(consultMessage.getTime())

            // Newly added
//            sendChatMessageInputRequestModel.setId(providerID);
            sendChatMessageInputRequestModel.setToken(token)
            sendChatMessageInputRequestModel.setProviderId(providerID)


            //Newly added retrofit


            chatViewModel?.sendChatMessageCall(sendChatMessageInputRequestModel,
                consultMessage.getId())?.observe(context) { sendChatMessageOutuputResponseModel1 ->

                if (sendChatMessageOutuputResponseModel1 != null && sendChatMessageOutuputResponseModel1.status != null && sendChatMessageOutuputResponseModel1.status!!) {

                    // Firebase uid
                    val mFirebaseUserProviderUid: String? =
                        PrefUtility().getFireBaseUid(context)


                    // Success response

                    if (sendChatMessageOutuputResponseModel1.status!!){
                        context.runOnUiThread {

                            // Dismiss progress bar
                            context.dismissProgressBar()
                            //Dismiss popup
                            if (popupWindow!!.isShowing) {
                                popupWindow!!.dismiss()
                                // Pushing the urgent parameter to firebase

                                mFirebaseDatabaseReference!!.child("consults").child(uID)
                                    .child("messages")
                                    .child(java.lang.String.valueOf(consultMessage.getId()))
                                    .child("urgent").setValue(urgent)
                                removeItem(holder)
                            }
                        }
                    } else {

                        // Failure response
                        val errMsg: String? = ErrorMessages().getErrorMessage(context,
                            java.lang.String.valueOf(sendChatMessageOutuputResponseModel1.errorMessage),
                            Constants.API.getHospital)
                        context.runOnUiThread {

                            // Dismiss progress bar
                            context.dismissProgressBar()
                            //Dismiss popup
                            if (popupWindow!!.isShowing) {
                                popupWindow!!.dismiss()
                            }
                            if (errMsg == null || errMsg.equals("null", ignoreCase = true)) {
                                CustomSnackBar.make(binding.getRoot(),
                                    context,
                                    CustomSnackBar.WARNING,
                                    Constants.API_ERROR,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0)?.show()
                            } else {
                                CustomSnackBar.make(binding.getRoot(),
                                    context,
                                    CustomSnackBar.WARNING,
                                    errMsg,
                                    CustomSnackBar.TOP,
                                    3000,
                                    0)?.show()
                            }
                        }
                    }
                } else {
                    // Failure response
                    val errMsg: String? = ErrorMessages().getErrorMessage(context,
                        java.lang.String.valueOf(sendChatMessageOutuputResponseModel1?.errorMessage),
                        Constants.API.getHospital)
                    context.runOnUiThread {

                        // Dismiss progress bar
                        context.dismissProgressBar()
                        //Dismiss popup
                        if (popupWindow!!.isShowing) {
                            popupWindow!!.dismiss()
                        }
                        if (errMsg == null || errMsg.equals("null", ignoreCase = true)) {
                            CustomSnackBar.make(binding.getRoot(),
                                context,
                                CustomSnackBar.WARNING,
                                Constants.API_ERROR,
                                CustomSnackBar.TOP,
                                3000,
                                0)?.show()
                        } else {
                            CustomSnackBar.make(binding.getRoot(),
                                context,
                                CustomSnackBar.WARNING,
                                errMsg,
                                CustomSnackBar.TOP,
                                3000,
                                0)?.show()
                        }
                    }
                }
            }
        } catch (e: Exception) {

        }
        //        });
    }

    private fun removeItem(holder: UrgentMessageAdapter.ViewHolder) {
        val newPosition: Int = holder.getAdapterPosition()
        messageList.removeAt(newPosition)
        notifyItemRemoved(newPosition)
        notifyItemRangeChanged(newPosition, messageList.size)
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

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setListener(l: AttachmentListener?) {
        listen = l
    }

    private enum class MessageType {
        system, text, Discharged, Exit, image, file, video
    }

    interface AttachmentListener {
        fun onItemClicked(url: String?, type: String?)
    }

    class ViewHolder(itemBinding: ItemUrgentMessagesBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        var binding: ItemUrgentMessagesBinding = itemBinding
        private fun removeItem() {}

    }

    companion object {
        private val TAG: String = ViewUrgentMessagesActivity::class.java.getSimpleName()
        private const val LOADING_IMAGE_URL =
            "https://firebasestorage.googleapis.com/v0/b/omnicure.appspot.com/o/spin-32.gif?alt=media&token=f5a877ef-bf5b-4d54-85da-a30a5d8ce98d" //"https://www.google.com/images/spin-32.gif";
        private var inflater: LayoutInflater? = null
    }

    init {
        this.context = context
        messageList = list
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        encKey = key
        senderId = sid
        uID = strUID
        setHasStableIds(true)
    }
}