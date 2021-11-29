package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogoutRequestBody(token: String?, userId: Long?) {
    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("id")
    private var id: Long? = null

    fun LogoutRequestBody(token: String?, id: Long?) {
        this.token = token
        this.id = id
    }
}