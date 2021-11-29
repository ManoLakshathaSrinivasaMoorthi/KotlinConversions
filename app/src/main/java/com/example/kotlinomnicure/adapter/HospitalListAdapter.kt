package com.example.kotlinomnicure.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.HospitalNameChildBinding
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import java.util.*

class HospitalListAdapter(private var hospitalRecyclerListener: HospitalRecyclerListener?, hospitalList: List<Hospital?>?, selectedHosp: String) : RecyclerView.Adapter<HospitalListAdapter.ViewHolder>(),Filterable{


    private var selectedHospital: String? = selectedHosp
    private var hospitalList: List<Hospital>? = null
    private var hospitalListFiltered: List<Hospital>? = hospitalList as List<Hospital>?

    init { (hospitalList as List<Hospital>?).also { this.hospitalList = it } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: HospitalNameChildBinding =
            DataBindingUtil.inflate(inflater, R.layout.hospital_name_child, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setHospitalList(holder, position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return hospitalListFiltered!!.size
    }

    private fun setHospitalList(holder: ViewHolder, position: Int) {
        val hospital = hospitalListFiltered!![position]
        holder.itemBinding.hospitalName.text = hospital.getName()
        if (selectedHospital == hospital.getName()) {
            holder.itemBinding.icSelected.visibility = View.VISIBLE
        } else holder.itemBinding.icSelected.visibility = View.GONE
        holder.itemView.setOnClickListener { hospitalRecyclerListener!!.onItemSelected(hospital) }
    }

    fun getHospitalRecyclerListener(): HospitalRecyclerListener? {
        return hospitalRecyclerListener
    }

    fun setHospitalRecyclerListener(recyclerListener: HospitalRecyclerListener?) {
        hospitalRecyclerListener = recyclerListener
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                hospitalListFiltered = if (charString.isEmpty()) {
                    hospitalList
                } else {
                    val filteredList: MutableList<Hospital> = ArrayList()
                    for (hospital in hospitalList!!) {
                        if (hospital.getName()!!.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT))) {
                            filteredList.add(hospital)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = hospitalListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                (filterResults.values as ArrayList<Hospital>).also { hospitalListFiltered = it }
                notifyDataSetChanged()
            }
        }
    }


    interface HospitalRecyclerListener {
        fun onItemSelected(hospital: Hospital?)
    }

    class ViewHolder(var itemBinding: HospitalNameChildBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}
