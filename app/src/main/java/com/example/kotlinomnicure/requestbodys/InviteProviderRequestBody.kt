package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InviteProviderRequestBody {
    @Expose
    @SerializedName("patientId")
    private var patientId: Long? = null

    @Expose
    @SerializedName("providerId")
    private var providerId: Long? = null

    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("id")
    private var id: Long? = null

    fun InviteProviderRequestBody(patientId: Long?, providerId: Long?, token: String?, id: Long?) {
        this.patientId = patientId
        this.providerId = providerId
        this.token = token
        this.id = id
    }
}