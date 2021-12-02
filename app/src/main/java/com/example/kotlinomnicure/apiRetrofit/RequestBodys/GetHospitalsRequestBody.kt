package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetHospitalsRequestBody {

    @Expose
    @SerializedName("status")
    private val status: String? = null

    @Expose
    @SerializedName("subRegionId")
    private val subRegionId: String? = null

    @Expose
    @SerializedName("regionId")
    private val regionId: String? = null

    @Expose
    @SerializedName("zipCode")
    private val zipCode: String? = null

    @Expose
    @SerializedName("country")
    private val country: String? = null

    @Expose
    @SerializedName("zone")
    private val zone: String? = null

    @Expose
    @SerializedName("state")
    private val state: String? = null

    @Expose
    @SerializedName("city")
    private val city: String? = null

    @Expose
    @SerializedName("name")
    private val name: String? = null

    @Expose
    @SerializedName("token")
    private val token: String? = null

    @Expose
    @SerializedName("id")
    private val id: String? = null
}