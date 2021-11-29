package com.example.kotlinomnicure.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.CensusHospitalNameChildBinding
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital

class CensusHospitalListViewAdapter(private var hospitalRecyclerListener: HospitalRecyclerListener?, private var hospitalList: List<Hospital>?, selectedHospId: Long) : RecyclerView.Adapter<CensusHospitalListViewAdapter.ViewHolder>() {


    private val selectedHospital: String? = null
    private var selectedHospitalId: Long = selectedHospId


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: CensusHospitalNameChildBinding =
            DataBindingUtil.inflate(inflater, R.layout.census_hospital_name_child, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setHospitalList(holder, position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return hospitalList!!.size
    }

    private fun setHospitalList(holder: ViewHolder, position: Int) {
        val hospital: Hospital = hospitalList!![position]
        holder.itemBinding.txtHospitalName.text = hospital.getName()
        if (selectedHospitalId == hospital.getId()) {
            holder.itemBinding.icSelected.isChecked = true
            holder.itemBinding.txtHospitalName.setTypeface(holder.itemBinding.txtHospitalName.typeface, Typeface.BOLD)
        } else {
            holder.itemBinding.icSelected.isChecked = false
            holder.itemBinding.txtHospitalName.setTypeface(holder.itemBinding.txtHospitalName.typeface, Typeface.NORMAL)
        }
        holder.itemView.setOnClickListener {
            selectedHospitalId = hospital.getId()!!
            notifyDataSetChanged()
            getHospitalRecyclerListener()!!.onItemSelected(hospital)
        }
        holder.itemBinding.icSelected.setOnClickListener { v ->
            val checkBox = v as CheckBox
            if (!checkBox.isChecked) {
                checkBox.isChecked = true
            }
            selectedHospitalId = hospital.getId()!!
            notifyDataSetChanged()
            getHospitalRecyclerListener()!!.onItemSelected(hospital)
        }
    }

    private fun getHospitalRecyclerListener(): HospitalRecyclerListener? {
        return hospitalRecyclerListener
    }

    fun setHospitalRecyclerListener(recyclerListener: HospitalRecyclerListener?) {
        hospitalRecyclerListener = recyclerListener
    }

    interface HospitalRecyclerListener : CensusHospitalListAdapter.HospitalRecyclerListener {
        override fun onItemSelected(hospital: Hospital?)
    }

    class ViewHolder(itemBinding: CensusHospitalNameChildBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var itemBinding: CensusHospitalNameChildBinding = itemBinding

    }
}


