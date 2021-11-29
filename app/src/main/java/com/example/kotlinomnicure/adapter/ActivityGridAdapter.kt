package com.example.kotlinomnicure.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.model.ConsultMessage
import com.example.kotlinomnicure.utils.AESUtils
import java.util.ArrayList

class ActivityGridAdapter {
    private var inflater: LayoutInflater? = null
    var context: Context? = null
    var messageList: ArrayList<ConsultMessage> = ArrayList<ConsultMessage>()
    var listen: AttachmentListener? = null
    var encKey: String? = null


    fun AttachmentGridAdapter(context: Context, list: ArrayList<ConsultMessage>, key: String?) {
        this.context = context
        messageList = list
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        encKey = key
        setHasStableIds(true)
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.item_attachment, parent, false)
        return ViewHolder(view)
    }

    fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message: ConsultMessage = messageList[position]
        val imageUrl: String = AESUtils().decryptData(message.getImageUrl(), encKey)

        if (message.getType().equals("file",ignoreCase = true)) {
            Glide.with(context)
                .load(R.drawable.ic_pdf)
                .centerCrop()
                .into(holder.img)
        } else {
            Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .into(holder.img)
        }
        if (message.getType().equalsIgnoreCase("video")) {
            holder.playIcon.visibility = View.VISIBLE
        } else {
            holder.playIcon.visibility = View.GONE
        }
        holder.container.setOnClickListener {
            listener!!.onItemClicked(imageUrl,
                message.getType())
        }
    }

    fun getItemCount(): Int {
        return messageList.size
    }

    fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setListener(l: AttachmentListener?) {
        listener = l
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