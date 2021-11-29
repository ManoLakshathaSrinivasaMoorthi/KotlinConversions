package com.example.dailytasksamplepoc.kotlinomnicure.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.HospitalNameChildBinding
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital

import java.util.ArrayList

class HospitalListAdapter(
    param: HospitalRecyclerListener,
    hospitalList: Any,
    selectedHosp: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var selectedHospital: String? = null
    private var hospitalRecyclerListener: HospitalRecyclerListener? = null
    private var hospitalList: List<Hospital>? = null
    private var hospitalListFiltered: List<Hospital>? = null


    fun HospitalListAdapter(
        hospitalRecyclerListener: HospitalRecyclerListener?,
        hospitalList: List<Hospital>?,
        selectedHosp: String?
    ) {
        this.hospitalRecyclerListener = hospitalRecyclerListener
        this.hospitalList = hospitalList
        hospitalListFiltered = hospitalList
        selectedHospital = selectedHosp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: HospitalNameChildBinding =
            DataBindingUtil.inflate(inflater, R.layout.hospital_name_child, parent, false)
        return ViewHolder(itemBinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setHospitalList(holder as ViewHolder, position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return hospitalListFiltered!!.size
    }

    private fun setHospitalList(holder: ViewHolder, position: Int) {
        val hospital: Hospital = hospitalListFiltered!![position]
        holder.itemBinding.hospitalName.setText(hospital.name)
        if (selectedHospital == hospital.name) {
            holder.itemBinding.icSelected.setVisibility(View.VISIBLE)
        } else {
            holder.itemBinding.icSelected.setVisibility(View.GONE)
        }
        holder.itemView.setOnClickListener { hospitalRecyclerListener!!.onItemSelected(hospital) }
    }

    fun getHospitalRecyclerListener(): HospitalRecyclerListener? {
        return hospitalRecyclerListener
    }

    fun setHospitalRecyclerListener(recyclerListener: HospitalRecyclerListener?) {
        hospitalRecyclerListener = recyclerListener
    }

    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                hospitalListFiltered = if (charString.isEmpty()) {
                    hospitalList
                } else {
                    val filteredList: MutableList<Hospital> = ArrayList<Hospital>()
                    for (hospital in hospitalList!!) {
                        if (hospital.name?.toLowerCase()?.contains(charString.toLowerCase()) == true) {
                            filteredList.add(hospital)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = hospitalListFiltered
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                hospitalListFiltered = filterResults.values as ArrayList<Hospital>
                notifyDataSetChanged()
            }
        }
    }


    interface HospitalRecyclerListener {
        fun onItemSelected(hospital: Hospital?)
    }

    class ViewHolder(itemBinding: HospitalNameChildBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        val itemBinding: HospitalNameChildBinding

        init {
            this.itemBinding = itemBinding
        }
    }


}