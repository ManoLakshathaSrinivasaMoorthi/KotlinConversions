package com.example.kotlinomnicure.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import omnicurekotlin.example.com.providerEndpoints.model.SystemAlerts

class AlertsResponse {
    @Expose
    @SerializedName("systemAlertList")
    var systemAlertList: List<SystemAlerts>? = null

    @Expose
    @SerializedName("errorId")
    var errorId = 0

    @Expose
    @SerializedName("errorMessage")
    var errorMessage: String? = null

    @Expose
    @SerializedName("status")
    var isStatus = false
    fun getStatus(): Boolean {
        return isStatus
    }
}