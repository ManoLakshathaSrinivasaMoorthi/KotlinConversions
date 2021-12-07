package com.example.kotlinomnicure.adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.kotlinomnicure.R

import com.example.kotlinomnicure.activity.ChatActivity
import com.example.kotlinomnicure.databinding.MembersDialogItemBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.activity.BaseActivity
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp
import omnicurekotlin.example.com.providerEndpoints.model.Members

class MembersDialogAdapter(
    baseActivity: ViewHolder,
    context: Context,
    members: List<Members?>?,
    param: CallbackDirectory
) : RecyclerView.Adapter<MembersDialogAdapter.ViewHolder>() {
    var memberList: List<Members>? = null
    var context: Context? = null
    var activity: Activity? = null
    var providerID = 0L
    private var callbackDirectory: CallbackDirectory? = null

    fun MembersDialogAdapter(
        activity: BaseActivity?,
        context: Context,
        objects: List<Members>,
        callback: CallbackDirectory?,
    ) {
        this.context = context
        memberList = objects
        this.activity = activity
        callbackDirectory = callback
        providerID = PrefUtility().getProviderId(context)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: MembersDialogItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.members_dialog_item, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member: Members = memberList!![position]
        var name: String? = member.getProviderName()
        if (name?.trim { it <= ' ' }?.length!! > 15) {
            name = name.substring(0, 15) + "..."
        }
        holder.itemBinding.name.setText(name)

        if (member.getRpType() != null) {
            holder.itemBinding.role.setText(member.getRpType())
        } else {
            holder.itemBinding.role.setText("")
        }
        if (context is ChatActivity) {
            holder.itemBinding.videoIcon.setVisibility(View.GONE)
        } else {
            holder.itemBinding.videoIcon.setVisibility(View.VISIBLE)
        }
        if (!TextUtils.isEmpty(member.getProfilePic())) {
            holder.itemBinding.imagePB.setVisibility(View.VISIBLE)
            Glide.with(holder.itemBinding.profileImg.getContext())
                .load(member.getProfilePic())
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        holder.itemBinding.imagePB.setVisibility(View.GONE)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        holder.itemBinding.defautlImg.setVisibility(View.GONE)
                        holder.itemBinding.imagePB.setVisibility(View.GONE)
                        holder.itemBinding.profileImg.setVisibility(View.VISIBLE)
                        holder.itemBinding.profileImg.setImageDrawable(resource)
                        return true
                    }
                })
                .into(holder.itemBinding.profileImg)
        } else {
            holder.itemBinding.defautlImg.setVisibility(View.VISIBLE)
            holder.itemBinding.defautlImg.setText(member.getProviderName()?.let {
                UtilityMethods().getNameText(it)
            })
            holder.itemBinding.profileImg.setVisibility(View.GONE)
        }
        if (member.getProviderId().equals(providerID.toString())) {
            holder.itemBinding.videoIcon.setVisibility(View.GONE)
            holder.itemBinding.audioIcon.setVisibility(View.GONE)
        } else {
            holder.itemBinding.videoIcon.setVisibility(View.VISIBLE)
            holder.itemBinding.audioIcon.setVisibility(View.VISIBLE)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return memberList!!.size
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    fun checkSelfPermissions() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO,
                ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO) &&
            checkSelfPermission(Manifest.permission.CAMERA, ConstantApp().PERMISSION_REQ_ID_CAMERA)
        ) {
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)
        }
    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
//        Log.i("checkSelfPermission ", permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(context!!,
                permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(permission),
                requestCode)
            return false
        }
        return true
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray, ) {

        when (requestCode) {
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO -> {
                checkSelfPermission(Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA)
            }
            ConstantApp().PERMISSION_REQ_ID_CAMERA -> {
            }
            else -> {
                Toast.makeText(context, "Please give permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface CallbackDirectory {
        fun onClickCallItem(provider: Members?, callType: String?)
    }

    class ViewHolder(itemBinding: MembersDialogItemBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        val itemBinding: MembersDialogItemBinding

        init {
            this.itemBinding = itemBinding
            itemBinding.audioIcon.setOnClickListener { view ->
                MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
                    override fun onClickCallItem(provider: Members?, callType: String?) {
                        startCall(context, provider, callType, mDialogView, consultProvider.getId())
                    }
                }).handleMultipleClick(itemBinding.audioIcon)
                MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
                    override fun onClickCallItem(provider: Members?, callType: String?) {
                        startCall(context, provider, callType, mDialogView, consultProvider.getId())
                    }
                }).checkSelfPermissions()
                MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
                    override fun onClickCallItem(provider: Members?, callType: String?) {
                        Constants.API.startCall(context,
                            provider,
                            callType,
                            mDialogView,
                            consultProvider.getId())
                    }
                }).callbackDirectory?.onClickCallItem(MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
                    override fun onClickCallItem(provider: Members?, callType: String?) {
                        Constants.API.startCall(context,
                            provider,
                            callType,
                            mDialogView,
                            consultProvider.getId())
                    }
                }).memberList?.get(adapterPosition),
                    Constants.FCMMessageType.AUDIO_CALL)
            }
            itemBinding.videoIcon.setOnClickListener { view ->
                MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
                    override fun onClickCallItem(provider: Members?, callType: String?) {
                        Constants.API.startCall(context,
                            provider,
                            callType,
                            mDialogView,
                            consultProvider.getId())
                    }
                }).handleMultipleClick(itemBinding.videoIcon)
                MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
                    override fun onClickCallItem(provider: Members?, callType: String?) {
                        Constants.API.startCall(context,
                            provider,
                            callType,
                            mDialogView,
                            consultProvider.getId())
                    }
                }).checkSelfPermissions()
                MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
                    override fun onClickCallItem(provider: Members?, callType: String?) {
                        Constants.API.startCall(context,
                            provider,
                            callType,
                            mDialogView,
                            consultProvider.getId())
                    }
                }).callbackDirectory?.onClickCallItem(MembersDialogAdapter(this, context, members, object : CallbackDirectory() {
                    override fun onClickCallItem(provider: Members?, callType: String?) {
                        Constants.API.startCall(context,
                            provider,
                            callType,
                            mDialogView,
                            consultProvider.getId())
                    }
                }).memberList?.get(adapterPosition),
                    Constants.FCMMessageType.VIDEO_CALL)
            }
        }
    }

}