package com.example.dailytasksamplepoc.kotlinomnicure.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RegistrationPhoneOtpRequestBody(
    @field:SerializedName("code") @field:Expose var code: String,
    @field:SerializedName(
        "phoneNumber"
    ) @field:Expose var phoneNumber: String,
    @field:SerializedName("id") @field:Expose var id: String
)