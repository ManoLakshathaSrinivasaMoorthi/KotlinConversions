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
        objects) {
        docBoxPatientList = objects
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }



    class Holder {
        var name: TextView? = null
        var dob: TextView? = null
    }
}
