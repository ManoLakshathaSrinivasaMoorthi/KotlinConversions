package com.example.dailytasksamplepoc.kotlinomnicure.adapter

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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.RemoteDirectoryChildBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.ChatActivity
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.model.ConstantApp
import com.example.kotlinomnicure.utils.Constants
import com.mvp.omnicure.kotlinactivity.utils.UtilityMethods
import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model.Provider

class RemoteDirectoryAdapter(
    requireActivity: FragmentActivity,
    context: Context?,
    providers: List<Provider>
) : RecyclerView.Adapter<RemoteDirectoryAdapter.ViewHolder>() {
    var providerList: List<Provider>? = null
    var context: Context? = null
    var getActivity: Activity? = null
    var listen: DirectoryClickListener? = null

    fun RemoteDirectoryAdapter(getActivity: Activity, context: Context?, objects: List<Provider>) {
        this.getActivity = getActivity
        this.context = context
        providerList = objects
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: RemoteDirectoryChildBinding =
            DataBindingUtil.inflate(inflater, R.layout.remote_directory_child, parent, false)
        val viewHolder = ViewHolder(itemBinding)
        itemBinding.imgAudio.setOnClickListener { view ->
            handleMultipleClick(itemBinding.imgAudio)
            checkSelfPermissions()
            listen!!.onAudioCallClicked(providerList!![viewHolder.adapterPosition])
        }
        itemBinding.videoIcon.setOnClickListener { view ->
            handleMultipleClick(itemBinding.videoIcon)
            checkSelfPermissions()
            listen!!.onVideoCallClicked(providerList!![viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val provider: Provider = providerList!![position]
        var name: String? = provider.getName()
        if (name != null) {
            if (name.trim { it <= ' ' }.length > 15) {
                name = name.substring(0, 15) + "..."
            }
        }
        holder.itemBinding.name.setText(name)

        if (provider.getRemoteProviderType() != null) {
            holder.itemBinding.role.setText(provider.getRemoteProviderType())
        } else {
            holder.itemBinding.role.setText("")
        }
        holder.itemBinding.defautlImg.setVisibility(View.VISIBLE)
        holder.itemBinding.defautlImg.setText(provider.getName()?.let {
            UtilityMethods().getNameText(
                it
            )
        })
        holder.itemBinding.profileImg.setVisibility(View.GONE)
        //        System.out.println("user name123 "+provider.getName()+" "+provider.getStatus());
        if (TextUtils.isEmpty(provider.getStatus()) || provider.getStatus()
                .equals(Constants.ProviderStatus.OffLine.toString(),ignoreCase = true)
            || provider.getStatus().equals(Constants.ProviderStatus.Approved.toString(),ignoreCase = true)
        ) {
            holder.itemBinding.statusIcon.setImageResource(R.drawable.ic_grey_circle)
            holder.itemBinding.imgAudio.setVisibility(View.INVISIBLE)
            holder.itemBinding.videoIcon.setVisibility(View.INVISIBLE)
        } else if (provider.getStatus()
                .equals(Constants.ProviderStatus.Active.toString(),ignoreCase = true) || provider.getStatus()
                .equals(Constants.ProviderStatus.AutoLock.toString(),ignoreCase = true)
        ) {
            holder.itemBinding.statusIcon.setImageResource(R.drawable.ic_green_circle)
            holder.itemBinding.imgAudio.setVisibility(View.VISIBLE)
            holder.itemBinding.videoIcon.setVisibility(View.VISIBLE)
        } else {
            holder.itemBinding.statusIcon.setImageResource(R.drawable.ic_grey_circle)
            holder.itemBinding.imgAudio.setVisibility(View.INVISIBLE)
            holder.itemBinding.videoIcon.setVisibility(View.INVISIBLE)
        }
        if (context is ChatActivity) {
            holder.itemBinding.imgAudio.setVisibility(View.GONE)
            holder.itemBinding.videoIcon.setVisibility(View.GONE)
        } else {
            holder.itemBinding.imgAudio.setVisibility(View.VISIBLE)
            holder.itemBinding.videoIcon.setVisibility(View.VISIBLE)
        }
        if (!TextUtils.isEmpty(provider.getProfilePicUrl())) {
//            holder.itemBinding.imagePB.setVisibility(View.VISIBLE);
            Glide.with(holder.itemBinding.profileImg.getContext())
                .load(provider.getProfilePicUrl())
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.itemBinding.imagePB.setVisibility(View.GONE)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
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
            holder.itemBinding.defautlImg.setText(provider.getName()?.let {
                UtilityMethods().getNameText(
                    it
                )
            })
            holder.itemBinding.profileImg.setVisibility(View.GONE)
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return providerList!!.size
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    fun setListener(l: DirectoryClickListener?) {
        listen = l
    }

    interface DirectoryClickListener {
        fun onAudioCallClicked(provider: Provider?)
        fun onVideoCallClicked(provider: Provider?)
    }

    class ViewHolder(itemBinding: RemoteDirectoryChildBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        val itemBinding: RemoteDirectoryChildBinding

        init {
            this.itemBinding = itemBinding
        }
    }

    fun checkSelfPermissions(): Boolean {
        return checkSelfPermission(
            Manifest.permission.RECORD_AUDIO,
            ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO
        ) &&
                checkSelfPermission(
                    Manifest.permission.CAMERA,
                    ConstantApp.PERMISSION_REQ_ID_CAMERA
                )
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
                getActivity!!, arrayOf(permission),
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
//        Log.i("onRequestPermissions", requestCode + " " + Arrays.toString(permissions)
//                + " " + Arrays.toString(grantResults));
        when (requestCode) {
            ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO -> {
                checkSelfPermission(
                    Manifest.permission.CAMERA,
                    ConstantApp.PERMISSION_REQ_ID_CAMERA
                )
            }
            ConstantApp.PERMISSION_REQ_ID_CAMERA -> {
            }
            else -> {
                Toast.makeText(context, "Please give permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
}