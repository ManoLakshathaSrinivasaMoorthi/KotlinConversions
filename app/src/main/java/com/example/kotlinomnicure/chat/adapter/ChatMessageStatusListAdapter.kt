package com.example.kotlinomnicure.chat.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ItemViewChatMessageStatusBinding
import com.example.kotlinomnicure.utils.ChatUtils
import com.example.kotlinomnicure.utils.UtilityMethods
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import omnicurekotlin.example.com.providerEndpoints.model.ChatMessageStatusModel
import omnicurekotlin.example.com.providerEndpoints.model.Members
import java.util.ArrayList


class ChatMessageStatusListAdapter(
    var context: Context,
    arr: ArrayList<ChatMessageStatusModel>,
    read: Boolean,
    membersList: ArrayList<Members>?
) : RecyclerView.Adapter<ChatMessageStatusListAdapter.ViewHolder?>() {
    var messageList: ArrayList<ChatMessageStatusModel> = ArrayList<ChatMessageStatusModel>()
    var read: Boolean
    private val TAG = ChatMessageStatusListAdapter::class.java.simpleName
    var membersList: ArrayList<Members> = ArrayList<Members>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.getContext())
        val itemBinding: ItemViewChatMessageStatusBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_view_chat_message_status, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.itemBinding.userNameList.setText(messageList[position].providerName)
        holder.itemBinding.description.setText(messageList[position].providerType)
        var time: Long = 0
        time = messageList[position].time


        // Time stamp
        if (!TextUtils.isEmpty(ChatUtils().getStatusDateFormat(time))) holder.itemBinding.date.setText(
            ChatUtils().getStatusDateFormat(time))

        // User profile pic loading
        if (messageList[position].profilePicUrl != null) {
            if (!TextUtils.isEmpty(messageList[position].profilePicUrl)) {
                holder.itemBinding.profileImg.setVisibility(View.INVISIBLE)
                holder.itemBinding.imagePB.setVisibility(View.VISIBLE)

                // Downloading the image url from storage reference
                val storageReference: StorageReference? = messageList[position].profilePicUrl?.let {
                    FirebaseStorage.getInstance()
                        .getReferenceFromUrl(it)
                }
                storageReference?.getDownloadUrl()?.addOnCompleteListener { task ->
                    if (task.isSuccessful()) {
                        val downloadUrl: String = task.getResult().toString()

                        if (!(context is Activity && (context as Activity).isFinishing())) Glide.with(
                            context)
                            .load(downloadUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .listener(object : RequestListener<Drawable?> {




                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable?>?,
                                    isFirstResource: Boolean): Boolean {
                                    holder.itemBinding.imagePB.setVisibility(View.GONE)
                                    holder.itemBinding.defautlImg.setVisibility(View.VISIBLE)
                                    holder.itemBinding.defautlImg.setText(
                                        messageList[position].providerName?.let {
                                            UtilityMethods().getNameText(
                                                it
                                            )
                                        })
                                    holder.itemBinding.profileImg.setVisibility(View.GONE)
                               return true
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable?>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    holder.itemBinding.imagePB.setVisibility(View.GONE)
                                    holder.itemBinding.defautlImg.setVisibility(View.GONE)
                                    holder.itemBinding.profileImg.setVisibility(View.VISIBLE)
                                    //                                                Log.e(TAG, "onResourceReady: resource-->"+resource );
                                    holder.itemBinding.profileImg.setImageDrawable(resource)
                                    return false
                                }
                            })
                            .into(holder.itemBinding.profileImg)
                    } else {
//
                    }
                }

            } else {
                holder.itemBinding.defautlImg.setVisibility(View.VISIBLE)
                holder.itemBinding.defautlImg.setText(messageList[position].providerName?.let {
                    UtilityMethods().getNameText(
                        it
                    )
                })
                holder.itemBinding.profileImg.setVisibility(View.GONE)
            }
        } else {
            holder.itemBinding.defautlImg.setVisibility(View.VISIBLE)
            holder.itemBinding.defautlImg.setText(messageList[position].providerName?.let {
                UtilityMethods().getNameText(
                    it
                )
            })
            holder.itemBinding.profileImg.setVisibility(View.GONE)
        }

//        }
    }

    private val itemCount: Int = messageList.size
    override fun getItemCount(): Int {
        return itemCount
    }

    class ViewHolder(itemView: ItemViewChatMessageStatusBinding) :
        RecyclerView.ViewHolder(itemView.getRoot()) {
        var itemBinding: ItemViewChatMessageStatusBinding
        @JvmName("getItemBinding1")
        fun getItemBinding(): ItemViewChatMessageStatusBinding {
            return itemBinding
        }

        @JvmName("setItemBinding1")
        fun setItemBinding(itemBinding: ItemViewChatMessageStatusBinding) {
            this.itemBinding = itemBinding
        }

        init {
            itemBinding = itemView
        }
    }

    companion object {
        private var inflater: LayoutInflater? = null
    }

    init {
        messageList.addAll(arr)
        this.read = read
        this.membersList.addAll(membersList!!)
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


}