package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetProviderListRequestBody(role: String, token: String, providerId: Long) {
    @Expose
    @SerializedName("role")
    private var role: String? = null

    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("providerId")
    private var providerId: Long = 0

    fun GetProviderListRequestBody(role: String?, token: String?, providerId: Long) {
        this.role = role
        this.token = token
        this.providerId = providerId
    }
}