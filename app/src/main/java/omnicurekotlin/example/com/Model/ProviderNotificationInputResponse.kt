package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable

class ProviderNotificationInputResponse : Serializable {

    @SerializedName("userId")
    @Expose
    var userId: Long? = null

    @SerializedName("mobileAcuity")
    @Expose
    var mobileAcuity: Boolean? = null

    @SerializedName("webAcuity")
    @Expose
    var webAcuity: Boolean? = null

    @SerializedName("newpatient")
    @Expose
    var newpatient: Boolean? = null

    @SerializedName("notification")
    @Expose
    var notification: String? = null


    @SerializedName("id")
    @Expose
    var id: Long? = null


    companion object {
        private const val serialVersionUID = 290888232120126751L
    }
}