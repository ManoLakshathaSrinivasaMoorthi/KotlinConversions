package com.example.kotlinomnicure.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonResponseRetro {

    @Expose
    @SerializedName("dischargedCount")
    private var dischargedCount = 0

    @Expose
    @SerializedName("errorMessage")
    private var errorMessage: String? = null

    @Expose
    @SerializedName("errorId")
    private var errorId = 0

    @Expose
    @SerializedName("status")
    private var status = false

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

