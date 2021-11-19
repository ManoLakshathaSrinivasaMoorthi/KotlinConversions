package com.example.kotlinomnicure.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.utils.ChatUtils
import omnicurekotlin.example.com.patientsEndpoints.model.AthenaDeviceData

class AthenaDeviceListAdapter(context: Context, resource: Int, objects: List<AthenaDeviceData>) : ArrayAdapter<AthenaDeviceData>(context, resource, objects) {

    private var athenaDeviceDataList: List<AthenaDeviceData>? = objects
    private var inflater: LayoutInflater? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        var holder: Holder?
        if (view == null) {
            holder =Holder()
            view = inflater?.inflate(R.layout.doc_box_list_view, null, false)
            holder.name = view?.findViewById<View>(R.id.doc_box_patient_name) as TextView
            holder.dob = view.findViewById<View>(R.id.doc_box_patient_dob) as TextView
            view.tag = holder
        } else {
            holder = view.tag as Holder
        }
        val athenaDeviceData = athenaDeviceDataList!![position]
        val context: Context?=null
        holder.name?.setText((context?.getString(R.string.athena_hardware_id)) + " " + athenaDeviceData.getDeviceID1())
        if (athenaDeviceData.getUpdateTime() != null) {
            holder.dob?.setText((context?.getString(R.string.last_sync_time))
                        + " " + ChatUtils().getStatusDateFormat(athenaDeviceData.getUpdateTime()!!))
        } else {
            holder.dob?.setText(context?.getString(R.string.athena_empty_sync_time))
        }
        return view
    }
    
    class Holder {
        var name: TextView? = null
        var dob: TextView? = null
    }
}
