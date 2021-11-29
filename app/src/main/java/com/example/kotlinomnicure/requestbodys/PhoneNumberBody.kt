package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PhoneNumberBody {
    @Expose
    @SerializedName("phoneNumber")
    private var phoneNumber: String? = null

    fun PhoneNumberBody(phoneNumber: String?) {
        this.phoneNumber = phoneNumber
    }
}