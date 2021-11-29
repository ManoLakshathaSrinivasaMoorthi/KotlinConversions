package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TeamDetailsByNameRequestBody {
    @Expose
    @SerializedName("patientId")
    private var patientId: Long? = null

    @Expose
    @SerializedName("teamName")
    private var teamName: String? = null

    fun TeamDetailsByNameRequestBody(patientId: Long?, teamName: String?) {
        this.patientId = patientId
        this.teamName = teamName
    }
}