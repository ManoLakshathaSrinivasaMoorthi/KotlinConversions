package com.example.kotlinomnicure.apiRetrofit.RequestBodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonIdRequestBody(userid: Long) {
    @Expose
    @SerializedName("id")
    private var id: Long? = null

    fun CommonIdRequestBody(id: Long?) {
        this.id = id
    }
}