package com.example.kotlinomnicure.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.ActivityEnotes
import com.example.kotlinomnicure.media.Utils
import com.example.kotlinomnicure.model.HandOffList
import com.example.kotlinomnicure.utils.PrefUtility
import omnicurekotlin.example.com.providerEndpoints.model.ChatMessageStatusModel
import java.util.ArrayList

class ProgressAdapter: RecyclerView.Adapter<ProgressAdapter.ViewHolder>() {
    private var context: Context? = null
    private var messages: List<HandOffList>? = null
    private val readStatusArr = ArrayList<Boolean>()
    private var uid: String? = null

    fun ProgressAdapter(context: Context?, message: List<HandOffList>?) {
        this.context = context
        messages = message
        uid = context?.let { PrefUtility().getFireBaseUid(it) }
        for (position in messages!!.indices) {
            val consultMessage: HandOffList = messages!![position]
            readStatusArr.add(position, true)
            if (consultMessage.receiverList != null) {
                for (i in 0 until consultMessage.receiverList!!.size) {
                    val rec: ChatMessageStatusModel = consultMessage.receiverList!!.get(i)
                    if (rec.receiverId.equals(uid,ignoreCase = true) && rec.status
                            .equals("Received")
                    ) {
                        readStatusArr[position] = false
                        break
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.enotes_handoff_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consultMessage: HandOffList = messages!![position]
        val id: String = java.lang.String.valueOf(consultMessage.messageId)
        if (id != null && id != "null") holder.id.text = id
        holder.name.setText(consultMessage.senderName)
        holder.type.setText(consultMessage.subType.toString() + " eNote")
        holder.time.setText(consultMessage.time?.let { Utils().timestampToDate(it) })
        if (consultMessage.receiverList != null) {
            for (i in 0 until consultMessage.receiverList!!.size) {
                val rec: ChatMessageStatusModel = consultMessage.receiverList!!.get(i)
                if (rec.receiverId.equals(uid,ignoreCase = true) && rec.status
                        .equals("Received")
                ) {
                    holder.unreadIcon.visibility = View.VISIBLE
                    break
                } else {
                    holder.unreadIcon.visibility = View.GONE
                }
            }
        }
        holder.details.setOnClickListener {
            (context as ActivityEnotes?)?.detailsClick(holder.adapterPosition,
                readStatusArr,
                messages!!)

        }
    }

    override fun getItemCount(): Int {
        return messages!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var details: RelativeLayout
        var type: TextView
        var name: TextView
        var id: TextView
        var time: TextView
        var unreadIcon: ImageView

        init {
            details = itemView.findViewById(R.id.llMessage)
            type = itemView.findViewById(R.id.handoff)
            name = itemView.findViewById(R.id.txtRPName)
            id = itemView.findViewById(R.id.txtName)
            time = itemView.findViewById(R.id.txtDateAndTime)
            unreadIcon = itemView.findViewById(R.id.unreadIcon)
        }
    }

    interface DetailsClick {
        fun detailsClick(
            position: Int,
            readStatus: ArrayList<Boolean?>?,
            messages: List<HandOffList?>?,
        )
    }
}