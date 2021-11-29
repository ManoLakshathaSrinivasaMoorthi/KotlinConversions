package com.example.kotlinomnicure.adapter

import android.graphics.Typeface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.CensusWardNameChildBinding
import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientList

class CensusWardListViewAdapter(private var hospitalRecyclerListener: HospitalRecyclerListener?, private var wardPatientList: List<WardPatientList>?, defaultWardName: String?) : RecyclerView.Adapter<CensusWardListViewAdapter.ViewHolder>() {

    private var selectedWard: String? = null

    init {
        if (!TextUtils.isEmpty(defaultWardName)) {
            selectedWard = defaultWardName
        } else if (wardPatientList != null && wardPatientList!!.size > 1) {
            selectedWard = wardPatientList!![0].getWardName()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: CensusWardNameChildBinding =
            DataBindingUtil.inflate(inflater, R.layout.census_ward_name_child, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setHospitalList(holder, position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return wardPatientList!!.size
    }

    private fun setHospitalList(holder: ViewHolder, position: Int) {
        val ward: WardPatientList = wardPatientList!![position]
        holder.itemBinding.txtWardName.text = ward.getWardName()
        if (selectedWard?.let { ward.getWardName()?.contentEquals(it) } == true) {
            holder.itemBinding.icSelected.isChecked = true
            holder.itemBinding.txtWardName.setTypeface(
                holder.itemBinding.txtWardName.typeface,
                Typeface.BOLD
            )
        } else {
            holder.itemBinding.icSelected.isChecked = false
            holder.itemBinding.txtWardName.setTypeface(
                holder.itemBinding.txtWardName.typeface,
                Typeface.NORMAL
            )
        }
        holder.itemView.setOnClickListener { }
        holder.itemView.setOnClickListener { //                hospitalRecyclerListener.onItemSelected(ward);
            selectedWard = ward.getWardName()
            getHospitalRecyclerListener()!!.onItemSelected(ward)
            notifyDataSetChanged()
        }


    }

    private fun getHospitalRecyclerListener(): HospitalRecyclerListener? {
        return hospitalRecyclerListener
    }

    fun setHospitalRecyclerListener(recyclerListener: HospitalRecyclerListener?) {
        hospitalRecyclerListener = recyclerListener
    }

    interface HospitalRecyclerListener {
        fun onItemSelected(ward: WardPatientList?)
    }

    class ViewHolder(var itemBinding: CensusWardNameChildBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}
