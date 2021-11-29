package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetProviderByIdRequestBody(providerId: Long, token: String, id: Long) {
    @Expose
    @SerializedName("providerId")
    private var providerId: Long? = null

    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("id")
    private var id: Long? = null

    fun GetProviderByIdRequestBody(providerId: Long?, token: String?, id: Long?) {
        this.providerId = providerId
        this.token = token
        this.id = id
    }
}