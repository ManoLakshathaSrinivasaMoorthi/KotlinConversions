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

class LocationListAdapter(param: HospitalRecyclerListener, hospitalList: HashMap<Long, ConsultProvider?>, selectedHospitalId: Long) : RecyclerView.Adapter<LocationListAdapter.ViewHolder>() {
    private var hospitalRecyclerListener: HospitalRecyclerListener? = null
    private var hospitalList: MutableList<ConsultProvider>? = null
    private var selectedHospitalId: Long = -1

    fun LocationListAdapter(
        hospitalRecyclerListener: HospitalRecyclerListener?,
        hospitalList: HashMap<Long?, ConsultProvider?>,
        selectedHospId: Long, ) {
        this.hospitalRecyclerListener = hospitalRecyclerListener
        this.hospitalList = ArrayList<ConsultProvider>(hospitalList.values)
        this.selectedHospitalId = selectedHospId
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

    fun addItem(consultProvider: ConsultProvider) {
        if (hospitalList != null) {
            hospitalList!!.add(consultProvider)
            notifyDataSetChanged()
        }
    }

    interface HospitalRecyclerListener {
        fun onItemSelected(hospital: ConsultProvider?)
    }

    class ViewHolder(itemBinding: LocationListChildBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {
        private val itemBinding: LocationListChildBinding = itemBinding
        var hospital: ConsultProvider? = null
        fun setHospitalList(hospital: ConsultProvider) {
            this.hospital = hospital
            itemBinding.hospitalName.setText(hospital.getHospital())
            if (LocationListAdapter(object : HospitalRecyclerListener {
                    override fun onItemSelected(hospital: ConsultProvider) {
                        selectedHospitalId = hospital.getHospitalId()!!
                        //Filter active screen by score
                        if (hospital.getName() == null) {
                            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                                filterdeselectactive()
                            } else {
                            }
                        } else {
                            filterselectactive()
                        }
                        filterByAcuityActive(acuityLevel)
                        dialog.dismiss()
                        filterByDialog!!.dismiss()
                    }
                }, hospitalList, selectedHospitalId).selectedHospitalId == hospital.getHospitalId()) {
                itemBinding.icSelected.setChecked(true)
            } else {
                itemBinding.icSelected.setChecked(false)
            }
        }

        init {
            //  itemBinding.icSelected.setEnabled(false);
            itemView.setOnClickListener {
                LocationListAdapter(object : HospitalRecyclerListener() {
                    override fun onItemSelected(hospital: ConsultProvider) {
                        selectedHospitalId = hospital.getHospitalId()!!
                        //Filter active screen by score
                        if (hospital.getName() == null) {
                            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                                filterdeselectactive()
                            } else {
                            }
                        } else {
                            filterselectactive()
                        }
                        filterByAcuityActive(acuityLevel)
                        dialog.dismiss()
                        filterByDialog!!.dismiss()
                    }
                }, hospitalList, selectedHospitalId).selectedHospitalId = hospital?.getHospitalId()!!
                LocationListAdapter(object : HospitalRecyclerListener() {
                    fun onItemSelected(hospital: ConsultProvider) {
                        selectedHospitalId = hospital.getHospitalId()!!
                        //Filter active screen by score
                        if (hospital.getName() == null) {
                            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                                filterdeselectactive()
                            } else {
                            }
                        } else {
                            filterselectactive()
                        }
                        filterByAcuityActive(acuityLevel)
                        dialog.dismiss()
                        filterByDialog!!.dismiss()
                    }
                }, hospitalList, selectedHospitalId).notifyDataSetChanged()
                LocationListAdapter(object : HospitalRecyclerListener() {
                    fun onItemSelected(hospital: ConsultProvider) {
                        selectedHospitalId = hospital.getHospitalId()!!
                        //Filter active screen by score
                        if (hospital.getName() == null) {
                            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                                filterdeselectactive()
                            } else {
                            }
                        } else {
                            filterselectactive()
                        }
                        filterByAcuityActive(acuityLevel)
                        dialog.dismiss()
                        filterByDialog!!.dismiss()
                    }
                }, hospitalList, selectedHospitalId).getHospitalRecyclerListener()?.onItemSelected(hospital)
            }
            itemBinding.icSelected.setOnClickListener(View.OnClickListener { v ->
                val checkBox = v as CheckBox
                if (!checkBox.isChecked) {
                    checkBox.isChecked = true
                }
                LocationListAdapter(object : HospitalRecyclerListener() {
                    fun onItemSelected(hospital: ConsultProvider) {
                        selectedHospitalId = hospital.getHospitalId()!!
                        //Filter active screen by score
                        if (hospital.getName() == null) {
                            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                                filterdeselectactive()
                            } else {
                            }
                        } else {
                            filterselectactive()
                        }
                        filterByAcuityActive(acuityLevel)
                        dialog.dismiss()
                        filterByDialog!!.dismiss()
                    }
                }, hospitalList, selectedHospitalId).selectedHospitalId = hospital?.getHospitalId()!!
                LocationListAdapter(object : HospitalRecyclerListener() {
                    fun onItemSelected(hospital: ConsultProvider) {
                        selectedHospitalId = hospital.getHospitalId()!!
                        //Filter active screen by score
                        if (hospital.getName() == null) {
                            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                                filterdeselectactive()
                            } else {
                            }
                        } else {
                            filterselectactive()
                        }
                        filterByAcuityActive(acuityLevel)
                        dialog.dismiss()
                        filterByDialog!!.dismiss()
                    }
                }, hospitalList, selectedHospitalId).notifyDataSetChanged()
                LocationListAdapter(object : HospitalRecyclerListener() {
                    fun onItemSelected(hospital: ConsultProvider) {
                        selectedHospitalId = hospital.getHospitalId()!!
                        //Filter active screen by score
                        if (hospital.getName() == null) {
                            if (acuityLevel === Constants.AcuityLevel.NA && urgencyLevelType === Constants.UrgencyLevel.NA) {
                                filterdeselectactive()
                            } else {
                            }
                        } else {
                            filterselectactive()
                        }
                        filterByAcuityActive(acuityLevel)
                        dialog.dismiss()
                        filterByDialog!!.dismiss()
                    }
                }, hospitalList, selectedHospitalId).getHospitalRecyclerListener()?.onItemSelected(hospital)
            })
        }
    }
}