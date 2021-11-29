package com.example.kotlinomnicure.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.utils.ChatUtils
import omnicurekotlin.example.com.patientsEndpoints.model.DocBoxPatient

class DocBoxPatientListAdapter: ArrayAdapter<DocBoxPatient>{

    var docBoxPatientList: List<DocBoxPatient>? = null
    private var inflater: LayoutInflater? = null

    constructor(context: Context, resource: Int, objects: List<DocBoxPatient>):   super(
        context,
        resource,
        objects
    ) {
        docBoxPatientList = objects
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

//    fun getView(position: Int, view: View?, parent: ViewGroup?): View {
//        var view = view
//        var holder: Holder? = null
//        if (view == null) {
//            holder = Holder()
//            view =inflater?.inflate(R.layout.doc_box_list_view, null, false)
//            holder.name = view?.findViewById<View>(R.id.doc_box_patient_name) as TextView
//            holder.dob = view.findViewById<View>(R.id.doc_box_patient_dob) as TextView
//            view.tag = holder
//        } else {
//            holder = view.tag as Holder
//        }
//        val docBoxPatient = docBoxPatientList!![position]
//        if (docBoxPatient != null) {
//            val firstName = if (docBoxPatient.getFname() != null) docBoxPatient.getFname()!!
//                .trim() else ""
//            val lastName = if (docBoxPatient.getLname() != null) docBoxPatient.getLname()!!
//                .trim() else ""
//            if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
//                if (!TextUtils.isEmpty(docBoxPatient.getName())) {
//                    holder.name?.setText(docBoxPatient.getName())
//                } else if (docBoxPatient.getDocBoxPatientId() != null) {
//                    holder.name?.setText(docBoxPatient.getDocBoxPatientId())
//                }
//            } else {
//                holder.name?.setText("$firstName $lastName")
//            }
//            if (docBoxPatient.getDob() != null) {
//                holder.dob?.setText(
//                    context.getString(R.string.dob_with_colon) + ChatUtils().getDateFormat(
//                        docBoxPatient.getDob()!!
//                    )
//                )
//            }
//        }
//        return view
//    }


    class Holder {
        var name: TextView? = null
        var dob: TextView? = null
    }
}
