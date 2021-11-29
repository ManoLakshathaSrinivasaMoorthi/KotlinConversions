package com.mvp.omnicure.kotlinactivity.adapter

import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mvp.omnicure.R

import com.mvp.omnicure.databinding.SystemAlertChildListBinding
import com.mvp.omnicure.kotlinactivity.activity.SystemAlertActivity
import com.mvp.omnicure.utils.ChatUtils
import omnicure.mvp.com.providerEndpoints.model.SystemAlerts

import java.lang.Exception

class SystemAlertAdapter(systemAlertActivity: SystemAlertActivity, systemAlerts: SystemAlerts?) : RecyclerView.Adapter<SystemAlertAdapter.ViewHolder>() {
    private var systemAlertActivity: SystemAlertActivity? = null
    private var systemAlerts: SystemAlerts? = null
    private var systemAlertRecyclerListener: SystemAlertRecyclerListener? = null
    private val inflater: LayoutInflater? = null

    fun SystemAlertAdapter(systemAlertActivity: SystemAlertActivity?, systemAlerts: SystemAlerts?) {
        this.systemAlertActivity = systemAlertActivity
        this.systemAlerts = systemAlerts
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: SystemAlertChildListBinding =
            DataBindingUtil.inflate(inflater, R.layout.system_alert_child_list, parent, false)
        return ViewHolder(itemBinding)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: SystemAlertAdapter.ViewHolder, position: Int) {
        val systemAlert = systemAlerts!!.systemAlertList[position]
        holder.itemBinding.systemAlertTitle.setText(systemAlert.title)
        holder.itemBinding.systemAlertCreatedTime.setText(ChatUtils.getTimeAgo(systemAlert.createdTime.toLong()))
        holder.itemBinding.systemAlertDisplayMessage.setText(
            Html.fromHtml(
                systemAlerts!!.systemAlertList[position].alertMsg,
                Html.FROM_HTML_MODE_COMPACT
            )
        )
        holder.itemBinding.systemAlertDisplayMessage.setMovementMethod(LinkMovementMethod.getInstance())
    }
    override fun getItemCount(): Int {
        val count: Int
        count = try {
            systemAlerts!!.systemAlertList.size
        } catch (e: Exception) {
            return 0
        }
        return count
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setSystemAlertRecyclerListener(systemAlertRecyclerListener: SystemAlertAdapter.SystemAlertRecyclerListener) {
        this.systemAlertRecyclerListener = systemAlertRecyclerListener
    }

    interface SystemAlertRecyclerListener {
        fun onItemSelected()
    }

    class ViewHolder internal constructor(itemBinding: SystemAlertChildListBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        var  itemBinding: SystemAlertChildListBinding = itemBinding

    }


}