package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateFcmkKeyRequestBody {


    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("id")
    private var id: Long? = null

    fun UpdateFcmkKeyRequestBody(token: String?, id: Long?) {
        this.token = token
        this.id = id
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?) {
        this.id = id
    }
}