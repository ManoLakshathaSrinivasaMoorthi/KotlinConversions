package com.example.kotlinomnicure.apiRetrofit.RequestBodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetReadStatusRequestBody(
    @field:SerializedName("providerId") @field:Expose private val providerId: Long,
    @field:SerializedName(
        "patientId") @field:Expose private val patientId: Long,
    @field:SerializedName("messageId") @field:Expose private val messageId: String
)