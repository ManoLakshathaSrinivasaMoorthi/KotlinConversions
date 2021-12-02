package com.example.kotlinomnicure.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filter.FilterResults
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ItemChildCensusListBinding
import omnicurekotlin.example.com.hospitalEndpoints.model.Hospital
import java.util.ArrayList

class CensusHospitalListAdapter: RecyclerView.Adapter<CensusHospitalListAdapter.ViewHolder>(),
    Filterable {
    private var selectedHospital: String? = null
    private var hospitalRecyclerListener: HospitalRecyclerListener? = null
    private var hospitalList: List<Hospital>? = null
    private var hospitalListFiltered: List<Hospital>? = null
    private var onSearchResultListener: OnSearchResultListener? = null

    fun CensusHospitalListAdapter(
        hospitalRecyclerListener: HospitalRecyclerListener?,
        hospitalList: List<Hospital>?,
        selectedHosp: String?,
    ) {
        this.hospitalRecyclerListener = hospitalRecyclerListener
        this.hospitalList = hospitalList
        hospitalListFiltered = hospitalList
        selectedHospital = selectedHosp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: ItemChildCensusListBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_child_census_list, parent, false)
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

    fun updateList(hospitalList: List<Hospital>?) {
        if (hospitalList != null) {
            this.hospitalList = hospitalList
            hospitalListFiltered = hospitalList
            notifyDataSetChanged()
        }
    }

    fun getOnSearchResultListener(): OnSearchResultListener? {
        return onSearchResultListener
    }

    fun setOnSearchResultListener(onSearchResultListener: OnSearchResultListener?) {
        this.onSearchResultListener = onSearchResultListener
    }

    private fun setHospitalList(holder: ViewHolder, position: Int) {
        val hospital: Hospital = hospitalListFiltered!![position]
        holder.itemBinding.txtHospitalName.setText(hospital.getName())
        holder.itemBinding.txtAddress.setText(hospital.getSubRegionName())
        holder.itemView.setOnClickListener { hospitalRecyclerListener!!.onItemSelected(hospital) }
    }

    fun getHospitalRecyclerListener(): HospitalRecyclerListener? {
        return hospitalRecyclerListener
    }

    fun setHospitalRecyclerListener(recyclerListener: HospitalRecyclerListener?) {
        hospitalRecyclerListener = recyclerListener
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString().toLowerCase()
                hospitalListFiltered = if (charString.isEmpty()) {
                    hospitalList
                } else {
                    val filteredList: MutableList<Hospital> = ArrayList<Hospital>()
                    for (hospital in hospitalList!!) {
                        if (hospital.getName()?.toLowerCase()?.contains(charString.toLowerCase()) == true ||
                            hospital.getSubRegionName()?.toLowerCase()
                                ?.contains(charString.toLowerCase()) == true
                        ) {
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
                hospitalListFiltered = filterResults.values as ArrayList<Hospital>
                if (getOnSearchResultListener() != null) {
                    getOnSearchResultListener()!!.onSearchResult(hospitalListFiltered!!.size)
                }
                notifyDataSetChanged()
            }
        }
    }

    interface OnSearchResultListener {
        fun onSearchResult(count: Int)
    }

    interface HospitalRecyclerListener {
        fun onItemSelected(hospital: Hospital?)
    }

    class ViewHolder(itemBinding: ItemChildCensusListBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        val itemBinding: ItemChildCensusListBinding

        init {
            this.itemBinding = itemBinding
        }
    }
}