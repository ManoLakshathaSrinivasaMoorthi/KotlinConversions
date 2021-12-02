package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.videocall.openvcall.model.Message
import java.util.ArrayList

class InChannelMessageListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var mMsgList: ArrayList<Message>? = null

    private var mInflater: LayoutInflater? = null

    constructor(activity: Activity, list: ArrayList<Message>?) {
        mInflater = activity.layoutInflater
        mMsgList = list
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg: Message = mMsgList!![position]
        val myHolder = holder as MessageHolder
        val sender: String? = msg.getSender()?.name
        if (TextUtils.isEmpty(sender)) {
            myHolder.itemView.setBackgroundResource(R.drawable.rounded_bg_blue)
        } else {
            myHolder.itemView.setBackgroundResource(R.drawable.rounded_bg)
        }
        myHolder.mMsgContent.text = msg.getContent()
    }

    override fun getItemCount(): Int {
        return mMsgList!!.size
    }

    override fun getItemId(position: Int): Long {
        return mMsgList!![position].hashCode().toLong()
    }

    class MessageHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        var mMsgContent: TextView = v.findViewById<View>(R.id.msg_content) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View? = mInflater?.inflate(R.layout.in_channel_message, parent, false)
        return v?.let { MessageHolder(it) }!!
    }
}
