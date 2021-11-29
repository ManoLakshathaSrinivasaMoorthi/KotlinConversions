package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveAuditCallRequestBody {
    @Expose
    @SerializedName("auditId")
    private var auditId: String? = null

    @Expose
    @SerializedName("callStatus")
    private var callStatus = false

    @Expose
    @SerializedName("type")
    private var type: String? = null

    @Expose
    @SerializedName("providerId")
    private var providerId: String? = null

    fun SaveAuditCallRequestBody(
        auditId: String?,
        callStatus: Boolean,
        type: String?,
        providerId: String?
    ) {
        this.auditId = auditId
        this.callStatus = callStatus
        this.type = type
        this.providerId = providerId
    }
}