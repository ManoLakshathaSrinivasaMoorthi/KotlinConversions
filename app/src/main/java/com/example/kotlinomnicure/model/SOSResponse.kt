package com.example.kotlinomnicure.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SOSResponse: Serializable {

    @SerializedName("auditId")
    private var auditId: String? = null

    @SerializedName("dischargedCount")
    private var dischargedCount = 0

    @SerializedName("providerList")
    private var providerList: List<ProviderList?>? = null

    @SerializedName("errorId")
    private var errorId = 0

    @SerializedName("errorMessage")
    private var errorMessage: String? = null

    @SerializedName("status")
    private var status = false

    fun getAuditId(): String? {
        return auditId
    }

    fun setAuditId(auditId: String?) {
        this.auditId = auditId
    }

    fun getDischargedCount(): Int {
        return dischargedCount
    }

    fun setDischargedCount(dischargedCount: Int) {
        this.dischargedCount = dischargedCount
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getProviderList(): List<ProviderList?>? {
        return providerList
    }

    fun setProviderList(providerList: List<ProviderList?>?) {
        this.providerList = providerList
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

    class ProviderList {
        @SerializedName("remoteProviderType")
        var remoteProviderType: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("id")
        var id: String? = null
    }
}
