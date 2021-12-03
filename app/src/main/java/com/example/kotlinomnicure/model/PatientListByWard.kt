package com.example.kotlinomnicure.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PatientListByWard: Serializable {

    @SerializedName("totalPatientCount")
    private var totalPatientCount = 0

    @SerializedName("wardPatientList")
    private var wardPatientList: List<WardPatientList?>? = null

    @SerializedName("hospitalId")
    private var hospitalId: String? = null

    @SerializedName("errorMessage")
    private var errorMessage = 0

    @SerializedName("errorId")
    private var errorId = 0

    @SerializedName("status")
    private var status = false

    fun getTotalPatientCount(): Int {
        return totalPatientCount
    }

    fun setTotalPatientCount(totalPatientCount: Int) {
        this.totalPatientCount = totalPatientCount
    }

    fun getWardPatientList(): List<WardPatientList?>? {
        return wardPatientList
    }

    fun setWardPatientList(wardPatientList: List<WardPatientList?>?) {
        this.wardPatientList = wardPatientList
    }

    fun getHospitalId(): String? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: String?) {
        this.hospitalId = hospitalId
    }

    fun getErrorMessage(): Int {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: Int) {
        this.errorMessage = errorMessage
    }

    fun getErrorId(): Int {
        return errorId
    }

    fun setErrorId(errorId: Int) {
        this.errorId = errorId
    }

    fun getStatus(): Boolean {
        return status
    }

    fun setStatus(status: Boolean) {
        this.status = status
    }

    class WardPatientList {
        @SerializedName("count")
        var count = 0

        @SerializedName("patientList")
        var patientList: List<PatientList>? = null

        @SerializedName("wardName")
        var wardName: String? = null
    }

    class PatientList {
        @Expose
        @SerializedName(value = "status", alternate = ["Status"])
        var status: String? = null

        @SerializedName("time")
        var time: String? = null

        @SerializedName("completed_by")
        var completed_by: String? = null

        @SerializedName("dateOfBirth")
        var dateOfBirth: String? = null

        @SerializedName("teamName")
        var teamName: String? = null

        @SerializedName("teamId")
        var teamId: String? = null

        @SerializedName("covidPositive")
        var covidPositive: String? = null

        @SerializedName("consultId")
        var consultId: String? = null

        @SerializedName("urgent")
        var urgent = false

        @SerializedName("patientCondition")
        var patientCondition: String? = null

        @SerializedName("oxygenSupplement")
        var oxygenSupplement = false

        @SerializedName("gcsScore")
        var gcsScore = 0

        @SerializedName("qSofaScore")
        var qSofaScore = 0

        @SerializedName("score")
        var score: String? = null

        @SerializedName("lastMessageTime")
        var lastMessageTime: String? = null

        @SerializedName("inviteTime")
        var inviteTime: String? = null

        @SerializedName("acceptTime")
        var acceptTime: String? = null

        @SerializedName("joiningTime")
        var joiningTime: String? = null

        @SerializedName("hospitalId")
        var hospitalId: String? = null

        @SerializedName("hospital")
        var hospital: String? = null

        @SerializedName("dob")
        var dob: String? = null

        @SerializedName("gender")
        var gender: String? = null

        @SerializedName("rdProviderName")
        var rdProviderName: String? = null

        @SerializedName("rdProviderId")
        var rdProviderId: String? = null

        @SerializedName("bdProviderName")
        var bdProviderName: String? = null

        @SerializedName("bdProviderId")
        var bdProviderId: String? = null

        @SerializedName("note")
        var note: String? = null

        @SerializedName("lname")
        var lname: String? = null

        @SerializedName("fname")
        var fname: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("id")
        var id: String? = null
    }
}
