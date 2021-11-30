package com.example.kotlinomnicure.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R

import com.example.kotlinomnicure.interfaces.OnPatientHistoryItemListener
import com.example.kotlinomnicure.utils.ChatUtils
import com.mvp.omnicure.activity.ChatActivity
import omnicurekotlin.example.com.patientsEndpoints.model.PatientHistory

class PatientChatHistoryAdapter : RecyclerView.Adapter<PatientChatHistoryAdapter.ViewHolder>() {
    private val TAG: Class<PatientChatHistoryAdapter> = PatientChatHistoryAdapter::class.java
    private var patientHistoryList: List<PatientHistory>? = null
    private var onItemClickListener: OnPatientHistoryItemListener? = null
    private var context: Context? = null

    fun PatientChatHistoryAdapter(context: Context?, patientHistoryList: List<PatientHistory>?) {
        this.context = context
        this.patientHistoryList = patientHistoryList
        onItemClickListener = context as ChatActivity?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_history_chat, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            onItemClickListener?.onClickChatHistory(viewHolder.adapterPosition, viewHolder)
        }
        viewHolder.iconImgView.setOnClickListener {
            onItemClickListener?.onClickChatHistory(viewHolder.adapterPosition, viewHolder)
        }
        viewHolder.closeIcon.setOnClickListener {
            onItemClickListener?.onCloseBtnClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val patientHistory: PatientHistory = patientHistoryList!![position]

        var prefix = context!!.getString(R.string.consultation_completed)
        holder.counterTxtView.text = (position + 1).toString() + "."
        if (!TextUtils.isEmpty(patientHistory.getDischargeMessage())) {
            prefix = if (!TextUtils.isEmpty(patientHistory.getRdProviderName())) {
                prefix + " by " + "<font color=black><b>" + patientHistory.getRdProviderName() + "</b></font>" + " with notes: "
            } else {
                "$prefix with notes: "
            }
            val str = prefix + patientHistory.getDischargeMessage()
            holder.chatHistoryTxtView.text = Html.fromHtml(str)
        } else {
            holder.chatHistoryTxtView.text = prefix
        }
        if (patientHistory.getDischargeTime() != null) {
            holder.timeTxtView.setText(ChatUtils().getStatusDateFormat(patientHistory.getDischargeTime()!!))
        } else {
            holder.timeTxtView.text = ""
        }
        holder.iconImgView.visibility = View.VISIBLE
    }

   override fun getItemCount(): Int {
        return if (patientHistoryList == null || patientHistoryList!!.isEmpty()) {
            0
        } else patientHistoryList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val counterTxtView: TextView = itemView.findViewById(R.id.counterTxt)
        val chatHistoryTxtView: TextView = itemView.findViewById(R.id.chatHistoryMsgTxt)
        val timeTxtView: TextView = itemView.findViewById(R.id.chatHistoryTimeTxt)
        val iconImgView: ImageView = itemView.findViewById(R.id.chatHistoryIcon)
        val closeIcon: ImageView = itemView.findViewById(R.id.closeIcon)

    }

}