package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HealthMonitorEventRequestBody {
    @Expose
    @SerializedName("screenName")
    private var screenName: String? = null

    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("providerId")
    private var providerId: String? = null

    fun HealthMonitorEventRequestBody(screenName: String?, token: String?, providerId: String?) {
        this.screenName = screenName
        this.token = token
        this.providerId = providerId
    }

    fun getScreenName(): String? {
        return screenName
    }

    fun setScreenName(screenName: String?) {
        this.screenName = screenName
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getProviderId(): String? {
        return providerId
    }

    fun setProviderId(providerId: String?) {
        this.providerId = providerId
    }
}