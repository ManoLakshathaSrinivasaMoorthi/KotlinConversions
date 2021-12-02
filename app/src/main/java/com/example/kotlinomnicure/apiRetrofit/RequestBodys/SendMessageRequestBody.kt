package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SendMessageRequestBody(
    callType: String,
    s: String,
    receiverId: Long,
    token: String,
    callerId: Long
) {
    @Expose
    @SerializedName("type")
    private var type: String? = null

    @Expose
    @SerializedName("message")
    private var message: String? = null

    @Expose
    @SerializedName("receiverId")
    private var receiverId: Long? = null

    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("providerId")
    private var providerId: Long? = null

    fun SendMessageRequestBody(
        type: String?,
        message: String?,
        receiverId: Long?,
        token: String?,
        providerId: Long?
    ) {
        this.type = type
        this.message = message
        this.receiverId = receiverId
        this.token = token
        this.providerId = providerId
    }
}