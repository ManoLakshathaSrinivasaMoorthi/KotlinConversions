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

class MembersDialogAdapter:RecyclerView.Adapter<MembersDialogAdapter.ViewHolder> {


    var memberList: List<Members>? = null
    var context: Context? = null
    var activity: Activity? = null
    var providerID = 0L
    private var callbackDirectory: CallbackDirectory? = null
    constructor()
    constructor(
        activity: BaseActivity?,
        context: Context,
        objects: List<Members>,
        callback: CallbackDirectory?
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
        val member = memberList!![position]
        var name = member.getProviderName()
        if (name!!.trim { it <= ' ' }.length > 15) {
            name = name.substring(0, 15) + "..."
        }
        holder.itemBinding.name.text = name
        //        System.out.println("names " + holder.itemBinding.name.getPaint().measureText("myText"));
        if (member.getRpType() != null) {
            holder.itemBinding.role.text = member.getRpType()
        } else {
            holder.itemBinding.role.text = ""
        }
        if (context is ChatActivity) {
            holder.itemBinding.videoIcon.visibility = View.GONE
        } else {
            holder.itemBinding.videoIcon.visibility = View.VISIBLE
        }
        if (!TextUtils.isEmpty(member.getProfilePic())) {
            holder.itemBinding.imagePB.visibility = View.VISIBLE
            Glide.with(holder.itemBinding.profileImg.context)
                .load(member.getProfilePic())
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.itemBinding.imagePB.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.itemBinding.defautlImg.visibility = View.GONE
                        holder.itemBinding.imagePB.visibility = View.GONE
                        holder.itemBinding.profileImg.visibility = View.VISIBLE
                        holder.itemBinding.profileImg.setImageDrawable(resource)
                        return true
                    }
                })
                .into(holder.itemBinding.profileImg)
        } else {
            holder.itemBinding.defautlImg.visibility = View.VISIBLE
            holder.itemBinding.defautlImg.setText(member.getProviderName()?.let {
                UtilityMethods().getNameText(
                    it
                )
            })
            holder.itemBinding.profileImg.visibility = View.GONE
        }
        if (member.getProviderId().equals(providerID.toString())) {
            holder.itemBinding.videoIcon.visibility = View.GONE
            holder.itemBinding.audioIcon.visibility = View.GONE
        } else {
            holder.itemBinding.videoIcon.visibility = View.VISIBLE
            holder.itemBinding.audioIcon.visibility = View.VISIBLE
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
        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO,
                ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO
            ) &&
            checkSelfPermission(Manifest.permission.CAMERA, ConstantApp().PERMISSION_REQ_ID_CAMERA)
        ) {
            checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ConstantApp().PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
//        Log.i("checkSelfPermission ", permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(
                context!!,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(permission),
                requestCode
            )
            return false
        }
        return true
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
//        Log.i("onRequestPermissions", requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
        when (requestCode) {
            ConstantApp().PERMISSION_REQ_ID_RECORD_AUDIO -> {
                checkSelfPermission(
                    Manifest.permission.CAMERA,
                    ConstantApp().PERMISSION_REQ_ID_CAMERA
                )
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

    class ViewHolder(val itemBinding: MembersDialogItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemBinding.audioIcon.setOnClickListener { view ->
               MembersDialogAdapter(). handleMultipleClick(itemBinding.audioIcon)
                MembersDialogAdapter(). checkSelfPermissions()
                MembersDialogAdapter(). callbackDirectory?.onClickCallItem(
                    MembersDialogAdapter().  memberList?.get(adapterPosition),
                    Constants.FCMMessageType.AUDIO_CALL
                )
            }
            itemBinding.videoIcon.setOnClickListener { view ->
                MembersDialogAdapter().handleMultipleClick(itemBinding.videoIcon)
                MembersDialogAdapter(). checkSelfPermissions()
                MembersDialogAdapter().callbackDirectory?.onClickCallItem(
                    MembersDialogAdapter(). memberList?.get(adapterPosition),
                    Constants.FCMMessageType.VIDEO_CALL
                )
            }
        }
    }

}
