package com.example.kotlinomnicure.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.databinding.LocationListChildBinding
import com.example.kotlinomnicure.model.ConsultProvider
import java.util.ArrayList
import java.util.HashMap

class LocationListAdapter: RecyclerView.Adapter<LocationListAdapter.ViewHolder> {


    private var hospitalRecyclerListener: HospitalRecyclerListener? = null
    private var hospitalList: List<ConsultProvider>? = null
    private var selectedHospitalId: Long = -1

    constructor()
    constructor(
        hospitalRecyclerListener: HospitalRecyclerListener?,
        hospitalList: HashMap<Long?, ConsultProvider?>,
        selectedHospId: Long
    ) {
        this.hospitalRecyclerListener = hospitalRecyclerListener
      //  this.hospitalList =hospitalList
        selectedHospitalId = selectedHospId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding: LocationListChildBinding =
            DataBindingUtil.inflate(inflater, R.layout.location_list_child, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setHospitalList(hospitalList!![position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return hospitalList!!.size
    }


    fun getHospitalRecyclerListener(): HospitalRecyclerListener? {
        return hospitalRecyclerListener
    }

    fun setHospitalRecyclerListener(recyclerListener: HospitalRecyclerListener?) {
        hospitalRecyclerListener = recyclerListener
    }

    fun addItem(consultProvider: ConsultProvider?) {
        if (hospitalList != null) {
            //hospitalList?.add(consultProvider)
            notifyDataSetChanged()
        }
    }

    interface HospitalRecyclerListener {
        fun onItemSelected(hospital: ConsultProvider?)
    }

    class ViewHolder(private val itemBinding: LocationListChildBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var hospital: ConsultProvider? = null
        fun setHospitalList(hospital: ConsultProvider?) {
            this.hospital = hospital
            itemBinding.hospitalName.text = hospital!!.getHospital()
            itemBinding.icSelected.isChecked = LocationListAdapter().selectedHospitalId == hospital.getHospitalId()
        }

        init {
            //  itemBinding.icSelected.setEnabled(false);
            itemView.setOnClickListener {
               LocationListAdapter().selectedHospitalId = hospital!!.getHospitalId()!!
                LocationListAdapter(). notifyDataSetChanged()
                LocationListAdapter(). getHospitalRecyclerListener()?.onItemSelected(hospital)
            }
            itemBinding.icSelected.setOnClickListener { v ->
                val checkBox = v as CheckBox
                if (!checkBox.isChecked) {
                    checkBox.isChecked = true
                }
                LocationListAdapter(). selectedHospitalId = hospital!!.getHospitalId()!!
                LocationListAdapter(). notifyDataSetChanged()
                LocationListAdapter(). getHospitalRecyclerListener()?.onItemSelected(hospital)
            }
        }
    }
}
