package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProviderNotificationDetailsRequestBody(providerId: Long) {
    @Expose
    @SerializedName("id")
    private var id: Long? = null

    fun ProviderNotificationDetailsRequestBody(id: Long?) {
        this.id = id
    }
}