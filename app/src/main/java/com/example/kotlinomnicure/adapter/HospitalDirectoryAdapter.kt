package com.example.dailytasksamplepoc.kotlinomnicure.adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.HospitalDirectoryChildBinding
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital

class HospitalDirectoryAdapter(context: Context?, hospitals: List<Hospital>) : RecyclerView.Adapter<HospitalDirectoryAdapter.ViewHolder>() {
    var hospitalList: List<Hospital>? = null
    var context: Context? = null
    var listen: HospitalClickListener? = null

    fun HospitalDirectoryAdapter(context: Context, objects: List<Hospital>) {
        this.context = context
        hospitalList = objects
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: HospitalDirectoryChildBinding =
            DataBindingUtil.inflate(inflater, R.layout.hospital_directory_child, parent, false)
        val viewHolder = ViewHolder(itemBinding)
        itemBinding.container.setOnClickListener { view ->
            handleMultipleClick(itemBinding.container)
            listen!!.onHospitalClicked(hospitalList!![viewHolder.adapterPosition])
        }
        return viewHolder
    }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hospital: Hospital = hospitalList!![position]
        val name: String? = hospital.getName()

        holder.itemBinding.name.setText(name)

        if (hospital.getSubRegionName() != null) {
            holder.itemBinding.region.setText(hospital.getSubRegionName())
        } else {
            holder.itemBinding.region.setText("")
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return hospitalList!!.size
    }

    private fun handleMultipleClick(view: View) {
        view.isEnabled = false
        Handler().postDelayed({ view.isEnabled = true }, 500)
    }

    fun setListener(l: HospitalClickListener?) {
        listen = l
    }

    interface HospitalClickListener {
        fun onHospitalClicked(hospital: Hospital?)
    }

    class ViewHolder(itemBinding: HospitalDirectoryChildBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        val itemBinding: HospitalDirectoryChildBinding

        init {
            this.itemBinding = itemBinding
        }
    }


}