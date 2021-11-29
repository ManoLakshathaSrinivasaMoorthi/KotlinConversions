package com.example.dailytasksamplepoc.kotlinomnicure.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RegistrationEmailOtpRequestBody(
    @field:SerializedName("email") @field:Expose var email: String, @field:SerializedName(
        "id"
    ) @field:Expose var id: String
)