package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SendMsgNotifyRequestBody {
    @Expose
    @SerializedName("patientId")
    private var patientId: Long? = null

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

    fun getPatientId(): Long? {
        return patientId
    }

    fun setPatientId(patientId: Long?) {
        this.patientId = patientId
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

    fun getReceiverId(): Long? {
        return receiverId
    }

    fun setReceiverId(receiverId: Long?) {
        this.receiverId = receiverId
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getProviderId(): Long? {
        return providerId
    }

    fun setProviderId(providerId: Long?) {
        this.providerId = providerId
    }
}