package com.mvp.omnicure.kotlinactivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.HandOffChildListBinding
import com.example.dailytasksamplepoc.kotlinomnicure.activity.HandOffPatientsActivity



import omnicurekotlin.example.com.providerEndpoints.model.HandOffListResponse
import java.lang.Exception

class HandOffPatientAdapter(
    handOffPatientsActivity: HandOffPatientsActivity,
    handOffListResponse: HandOffListResponse?
) : RecyclerView.Adapter<HandOffPatientAdapter.ViewHolder>() {

    private var handOffPatientsActivity: HandOffPatientsActivity? = null
    private var handOffListResponse: HandOffListResponse? = null
    var handOffRecyclerListener: HandOffRecyclerListener? = null

    fun HandOffPatientAdapter(
        handOffPatientsActivity: HandOffPatientsActivity?,
        handOffListResponse: HandOffListResponse?
    ) {
        this.handOffPatientsActivity = handOffPatientsActivity
        this.handOffListResponse = handOffListResponse
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: HandOffChildListBinding =
            DataBindingUtil.inflate(inflater, R.layout.hand_off_child_list, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setHandOffList(holder, position)
        holder.itemView.setOnClickListener {
            handOffRecyclerListener!!.onItemSelected(handOffListResponse!!.otherBspList?.get(position))
            println("getOtherBspList" + (handOffListResponse!!.otherBspList?.get(position) ))
        }
    }

    override fun getItemCount(): Int {
        val count: Int
        count = try {
            handOffListResponse!!.otherBspList!!.size
        } catch (e: Exception) {
            return 0
        }
        return count
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun setHandOffList(holder: ViewHolder, position: Int) {
        val otherBspList = handOffListResponse!!.otherBspList?.get(position)
        holder.itemBinding.txtHospitalName.setText(otherBspList?.name)
    }

    @JvmName("getHandOffRecyclerListener1")
    fun getHandOffRecyclerListener(): HandOffRecyclerListener? {
        return handOffRecyclerListener
    }

    @JvmName("setHandOffRecyclerListener1")
    fun setHandOffRecyclerListener(handOffRecyclerListener: HandOffRecyclerListener?) {
        this.handOffRecyclerListener = handOffRecyclerListener
    }


    interface HandOffRecyclerListener {
        fun onItemSelected(otherBspList: HandOffListResponse.OtherBspList?)
    }

    class ViewHolder internal constructor(itemBinding: HandOffChildListBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        val itemBinding: HandOffChildListBinding

        init {
            this.itemBinding = itemBinding
        }
    }
}