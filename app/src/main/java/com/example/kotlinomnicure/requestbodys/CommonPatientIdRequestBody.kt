package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonPatientIdRequestBody(providerId: Long) {
    @Expose
    @SerializedName("patientId")
    private var patientId: Long? = null

    fun CommonPatientIdRequestBody(patientId: Long?) {
        this.patientId = patientId
    }
}