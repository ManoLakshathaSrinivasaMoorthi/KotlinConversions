package com.example.kotlinomnicure.interfaces

import omnicurekotlin.example.com.appointmentEndpoints.model.Appointment

interface OnAppointmentItemClick {
    fun onClickAppointment(appointment: Appointment?)
}
