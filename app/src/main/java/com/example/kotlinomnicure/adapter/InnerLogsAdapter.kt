package com.example.kotlinomnicure.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

import com.example.kotlinomnicure.activity.EncUtil
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.utils.AESUtils

import com.example.kotlinomnicure.utils.PrefUtility

class InnerLogsAdapter(
    messages: MutableList<ENotesMessageList>,
    context: Context?,
    logDateAdapter: LogDateAdapter
) : RecyclerView.Adapter<InnerLogsAdapter.ViewHolder>() {
    private var messagesList: List<ENotesMessageList>? = null
    private var context: Context? = null
    private var logDateAdapter: LogDateAdapter? = null
    private var encKey: String? = null

    fun InnerLogsAdapter(
        list: List<ENotesMessageList>?,
        context: Context?,
        logDateAdapter: LogDateAdapter?
    ) {
        messagesList = list
        this.context = context
        this.logDateAdapter = logDateAdapter
        EncUtil().generateKey(this.context)
        encKey = context?.let { PrefUtility().getAESAPIKey(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        logDateAdapter!!.getLogsRecycler()!!.setRecycledViewPool(LogDateAdapter(applicationContext, eNotesList).recycledViewPool)
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activites_log_logs_item, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) holder.topLine.visibility =
            View.INVISIBLE else holder.topLine.visibility =
            View.VISIBLE
        if (position == messagesList!!.size - 1) holder.bottomLine.visibility =
            View.INVISIBLE else holder.bottomLine.visibility =
            View.VISIBLE
        val data: ENotesMessageList = messagesList!![position]

        holder.time.setText(data.getMessageTime())
        val message: String? = encKey?.let { AESUtils().decryptData(data.getMessage(), it) }
        holder.message.text = message?.trim { it <= ' ' }
    }

    override fun getItemCount(): Int {
        return messagesList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bottomLine: ImageView
        var topLine: ImageView
        var time: TextView
        var message: TextView

        init {
            bottomLine = itemView.findViewById(R.id.bottom_line)
            topLine = itemView.findViewById(R.id.topLine)
            time = itemView.findViewById(R.id.logTime)
            message = itemView.findViewById(R.id.messageTxt)
        }
    }
}