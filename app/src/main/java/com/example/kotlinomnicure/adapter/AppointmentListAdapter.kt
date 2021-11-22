package com.example.kotlinomnicure.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.interfaces.OnAppointmentItemClick
import com.example.kotlinomnicure.utils.ChatUtils
import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment
import java.lang.String

class AppointmentListAdapter(context: Context, objects: List<Appointment>, private var appointmentItemListener: OnAppointmentItemClick?) : RecyclerView.Adapter<AppointmentListAdapter.ViewHolder>() {
    var context: Context? = context
    private var appointmentList: List<Appointment>? = objects
    private var inflater: LayoutInflater? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater!!.inflate(R.layout.doc_box_list_view, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            appointmentItemListener!!.onClickAppointment(
                appointmentList!![viewHolder.adapterPosition]
            )
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointmentList!![position]
        val firstName = if (appointment.getFname() != null) appointment.getFname()!!.trim()
        else ""
        val lastName = if (appointment.getLname() != null) appointment.getLname()!!.trim()
        else ""
        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
            if (!TextUtils.isEmpty(appointment.getName())) {
                holder.name.text = appointment.getName()
            } else if (appointment.getId() != null) {
                holder.name.text = String.valueOf(appointment.getId())
            }
        } else {
            holder.name.text = "$firstName $lastName"
        }
        if (appointment.getDob() != null) {
            holder.dob.text = context?.getString(R.string.dob_with_colon) + ChatUtils().getDateFormat(appointment.getDob()!!)
        }
    }

    override fun getItemCount(): Int {
        return if (appointmentList == null || appointmentList!!.isEmpty()) 0
        else appointmentList!!.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.doc_box_patient_name)
        var dob: TextView = itemView.findViewById(R.id.doc_box_patient_dob)

    }
}
