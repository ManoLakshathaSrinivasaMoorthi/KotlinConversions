package com.example.kotlinomnicure.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import omnicurekotlin.example.com.providerEndpoints.model.SystemAlerts

class AlertsResponse {

    @Expose
    @SerializedName("systemAlertList")
    private var systemAlertList: List<SystemAlerts?>? = null

    @Expose
    @SerializedName("errorId")
    private var errorId = 0

    @Expose
    @SerializedName("errorMessage")
    private var errorMessage: String? = null

    @Expose
    @SerializedName("status")
    private var status = false

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun isStatus(): Boolean {
        return status
    }

    fun getSystemAlertList(): List<SystemAlerts?>? {
        return systemAlertList
    }

    fun setSystemAlertList(systemAlertList: List<SystemAlerts?>?) {
        this.systemAlertList = systemAlertList
    }

    fun getErrorId(): Int {
        return errorId
    }

    fun setErrorId(errorId: Int) {
        this.errorId = errorId
    }

    fun getStatus(): Boolean {
        return status
    }

    fun setStatus(status: Boolean) {
        this.status = status
    }

}
