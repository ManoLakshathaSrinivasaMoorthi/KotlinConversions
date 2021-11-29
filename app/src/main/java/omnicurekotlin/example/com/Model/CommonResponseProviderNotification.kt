package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.healthcareEndPoints.Model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable


class CommonResponseProviderNotification : Serializable {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null


    @SerializedName("errorId")
    @Expose
    var errorId: Double? = null

    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String? = null

    @SerializedName("dischargedCount")
    @Expose
    var dischargedCount: Int? = null
}