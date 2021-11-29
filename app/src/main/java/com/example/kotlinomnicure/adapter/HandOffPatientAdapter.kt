package com.example.kotlinomnicure.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.activity.HandOffPatientsActivity
import com.example.kotlinomnicure.databinding.HandOffChildListBinding
import omnicurekotlin.example.com.providerEndpoints.model.HandOffListResponse

class HandOffPatientAdapter(private var handOffPatientsActivity: HandOffPatientsActivity?, private var handOffListResponse: HandOffListResponse?) :
        RecyclerView.Adapter<HandOffPatientAdapter.ViewHolder>() {


    private var handOffRecyclerListener: HandOffRecyclerListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: HandOffChildListBinding = DataBindingUtil.inflate(inflater, R.layout.hand_off_child_list,
            parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setHandOffList(holder, position)
        holder.itemView.setOnClickListener { handOffRecyclerListener?.onItemSelected(
            handOffListResponse?.getOtherBspList()?.get(position))
            println("getOtherBspList" + handOffListResponse?.getOtherBspList()?.get(position))
        }
    }

    override fun getItemCount(): Int {
        val count: Int
        try {
            count = handOffListResponse?.getOtherBspList()?.size!!
        } catch (e: Exception) {
            return 0
        }
        return count
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun setHandOffList(holder: ViewHolder, position: Int) {
        val otherBspList: HandOffListResponse.OtherBspList? =
            handOffListResponse?.getOtherBspList()?.get(position)
        holder.itemBinding.txtHospitalName.text = otherBspList?.getName()
    }

    fun getHandOffRecyclerListener(): HandOffRecyclerListener? {
        return handOffRecyclerListener
    }

    fun setHandOffRecyclerListener(handOffRecyclerListener: HandOffRecyclerListener?) {
        this.handOffRecyclerListener = handOffRecyclerListener
    }


    interface HandOffRecyclerListener {
        fun onItemSelected(otherBspList: HandOffListResponse.OtherBspList?)
    }

    class ViewHolder internal constructor(var itemBinding: HandOffChildListBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}







