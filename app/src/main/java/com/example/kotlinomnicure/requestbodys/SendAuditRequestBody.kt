package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SendAuditRequestBody {
    @Expose
    @SerializedName("providerId")
    private var providerId: String? = null

    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("id")
    private var id: Long? = null

    fun SendAuditRequestBody(providerId: String?, token: String?, id: Long?) {
        this.providerId = providerId
        this.token = token
        this.id = id
    }
}