package com.example.kotlinomnicure.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.ActivityAttachmentFilter
import com.example.kotlinomnicure.model.ConsultMessage
import com.example.kotlinomnicure.utils.AESUtils
import java.util.ArrayList

class AttachmentGridAdapter(
    activityAttachmentFilter: ActivityAttachmentFilter,
    messageList: ArrayList<ConsultMessage>,
    encKey: String?
) : RecyclerView.Adapter<AttachmentGridAdapter.ViewHolder>() {
    private var inflater: LayoutInflater? = null
    var context: Context? = null
    var messageList: ArrayList<ConsultMessage> = ArrayList<ConsultMessage>()
    var listen: AttachmentListener? = null
    var encKey: String? = null


    fun AttachmentGridAdapter(context: Context, list: ArrayList<ConsultMessage>, key: String?) {
        this.context = context
        this.messageList = list
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.encKey = key
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater!!.inflate(R.layout.item_attachment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: ConsultMessage = messageList[position]
        val imageUrl: String? = message.getImageUrl()?.let { encKey?.let { it1 ->
            AESUtils().decryptData(it,
                it1)
        } }

        if (message.getType().equals("file",ignoreCase = true)) {
            context?.let {
                Glide.with(it)
                    .load(R.drawable.ic_pdf)
                    .centerCrop()
                    .into(holder.img)
            }
        } else {
            context?.let {
                Glide.with(it)
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.img)
            }
        }
        if (message.getType().equals("video",ignoreCase = true)) {
            holder.playIcon.visibility = View.VISIBLE
        } else {
            holder.playIcon.visibility = View.GONE
        }
        holder.container.setOnClickListener {
            listen!!.onItemClicked(imageUrl,
                message.getType())
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

    interface AttachmentListener {
        fun onItemClicked(url: String?, type: String?)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var playIcon: ImageView
        var img: ImageView
        var container: ConstraintLayout

        init {
            img = itemView.findViewById(R.id.img)
            playIcon = itemView.findViewById(R.id.play_icon)
            container = itemView.findViewById(R.id.container)
        }
    }
}