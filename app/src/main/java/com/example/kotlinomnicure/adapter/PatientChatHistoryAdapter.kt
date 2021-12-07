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
import com.example.kotlinomnicure.activity.ChatActivity
import omnicurekotlin.example.com.patientsEndpoints.model.PatientHistory

class PatientChatHistoryAdapter : RecyclerView.Adapter<PatientChatHistoryAdapter.ViewHolder> {

    private var patientHistoryList: List<PatientHistory>? = null
    private var onItemClickListener: OnPatientHistoryItemListener? = null
    private var context: Context? = null

    constructor(context: Context?, patientHistoryList: List<PatientHistory>?) {
        this.context = context
        this.patientHistoryList = patientHistoryList
        onItemClickListener = context as ChatActivity?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_history_chat, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener { //                Log.d(TAG, "onClick: " + viewHolder.getAdapterPosition());
            onItemClickListener?.onClickChatHistory(viewHolder.adapterPosition, viewHolder)
        }
        viewHolder.iconImgView.setOnClickListener { //                Log.d(TAG, "onClick: " + viewHolder.getAdapterPosition());
            onItemClickListener?.onClickChatHistory(viewHolder.adapterPosition, viewHolder)
        }
        viewHolder.closeIcon.setOnClickListener { //                Log.d(TAG, "onClick: " + viewHolder.getAdapterPosition());
            onItemClickListener?.onCloseBtnClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val patientHistory = patientHistoryList!![position]
        //        Log.d(TAG, "Current patientHistory" + new Gson().toJson(patientHistory));
        if (patientHistory != null) {
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
    }

    override fun getItemCount(): Int {
        return if (patientHistoryList == null || patientHistoryList!!.isEmpty()) {
            0
        } else patientHistoryList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val counterTxtView: TextView
        val chatHistoryTxtView: TextView
        val timeTxtView: TextView
        val iconImgView: ImageView
        val closeIcon: ImageView

        init {
            counterTxtView = itemView.findViewById(R.id.counterTxt)
            chatHistoryTxtView = itemView.findViewById(R.id.chatHistoryMsgTxt)
            timeTxtView = itemView.findViewById(R.id.chatHistoryTimeTxt)
            iconImgView = itemView.findViewById(R.id.chatHistoryIcon)
            closeIcon = itemView.findViewById(R.id.closeIcon)
        }
    }


}
