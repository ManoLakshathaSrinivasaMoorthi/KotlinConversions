package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SendMessageOnTopicRequestBody {
    @Expose
    @SerializedName("patientsId")
    private var patientsId: String? = null

    @Expose
    @SerializedName("type")
    private var type: String? = null

    @Expose
    @SerializedName("message")
    private var message: String? = null

    @Expose
    @SerializedName("receiverId")
    private var receiverId = 0

    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("providerId")
    private var providerId: Long? = null

    fun SendMessageOnTopicRequestBody(
        providerId: Long?,
        type: String?,
        message: String?,
        token: String?
    ) {

        this.type = type
        this.message = message
        this.token = token
        this.providerId = providerId
    }

    fun getPatientsId(): String? {
        return patientsId
    }

    fun setPatientsId(patientsId: String?) {
        this.patientsId = patientsId
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getReceiverId(): Int {
        return receiverId
    }

    fun setReceiverId(receiverId: Int) {
        this.receiverId = receiverId
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }
}