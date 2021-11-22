package com.example.kotlinomnicure.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.ItemChildCensusListBinding
import omnicurekotlin.example.com.hospitalEndpoints.model.WardPatientList
import java.util.*

class CensusWardListAdapter(hospitalRecyclerListener: HospitalRecyclerListener, wardPatientList: List<WardPatientList>?, private var selectedHospital: String?) : RecyclerView.Adapter<CensusWardListAdapter.ViewHolder>(), Filterable {

    private var hospitalRecyclerListener: HospitalRecyclerListener? = hospitalRecyclerListener
    private var wardPatientList: List<WardPatientList>? = null
    private var wardPatientListFiltered: List<WardPatientList>? = wardPatientList
    private var onSearchResultListener: OnSearchResultListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: ItemChildCensusListBinding = DataBindingUtil.inflate(inflater, R.layout.item_child_census_list, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setHospitalList(holder, position)
    }

    fun getOnSearchResultListener(): OnSearchResultListener? {
        return onSearchResultListener
    }

    fun setOnSearchResultListener(onSearchResultListener: OnSearchResultListener?) {
        this.onSearchResultListener = onSearchResultListener
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return wardPatientListFiltered!!.size
    }

    @SuppressLint("SetTextI18n")
    private fun setHospitalList(holder: ViewHolder, position: Int) {
        val ward: WardPatientList = wardPatientListFiltered!![position]
        holder.itemBinding.txtHospitalName.text = ward.getWardName()
        if (ward.getCount()!! > 1) {
            holder.itemBinding.txtAddress.text = ward.getCount().toString() + " patients"
        } else {
            holder.itemBinding.txtAddress.text = ward.getCount().toString() + " patient"
        }
        holder.itemView.setOnClickListener { hospitalRecyclerListener!!.onItemSelected(ward) }
    }

    fun updateList(wardPatientList: List<WardPatientList>?) {
        if (wardPatientList != null) {
            this.wardPatientList = wardPatientList
            wardPatientListFiltered = wardPatientList
            notifyDataSetChanged()
        }
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
                val charString = charSequence.toString().toLowerCase(Locale.ROOT)
                wardPatientListFiltered = if (charString.isEmpty()) {
                    wardPatientList
                } else {
                    val filteredList: MutableList<WardPatientList> = ArrayList<WardPatientList>()
                    for (patientList in wardPatientList!!) {
                        if (patientList.getWardName()?.toLowerCase(Locale.ROOT)
                                        ?.contains(charString.toLowerCase(Locale.ROOT)) == true) {
                            filteredList.add(patientList)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = wardPatientListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                wardPatientListFiltered = filterResults.values as ArrayList<WardPatientList>
                if (getOnSearchResultListener() != null) {
                    getOnSearchResultListener()!!.onSearchResult(wardPatientListFiltered!!.size)
                }
                notifyDataSetChanged()
            }
        }
    }

    interface OnSearchResultListener : CensusHospitalListAdapter.OnSearchResultListener {
        override fun onSearchResult(count: Int)
    }

    interface HospitalRecyclerListener {
        fun onItemSelected(ward: WardPatientList?)
    }

    class ViewHolder(var itemBinding: ItemChildCensusListBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}
