package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HospitalIdRequestBody(hospitalId: Long) {

    @Expose
    @SerializedName("hospitalId")
    private var hospitalId: Long? = null

    fun HospitalIdRequestBody(hospitalId: Long?) {
        this.hospitalId = hospitalId
    }
}