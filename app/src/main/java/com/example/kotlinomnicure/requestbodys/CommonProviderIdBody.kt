package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonProviderIdBody(providerId: Long) {
    @Expose
    @SerializedName("providerId")
    private var providerId: Long? = null

    fun CommonProviderIdBody(providerId: Long?) {
        this.providerId = providerId
    }
}