package com.example.dailytasksamplepoc.kotlinomnicure.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlinomnicure.utils.Constants

import java.io.Serializable
import java.util.*

class AcuityModel : Serializable {

    var id: Long? = null
    var patientsId: Long? = null
    var patientId: Long? = null
    var text: String? = null
    var name: String? = null
    var fname: String? = null
    var lname: String? = null
    var unread = 0
    var time: Long? = null
    var msgName: String? = null
    var wardName: String? = null
    var recordNumber: String? = null
    var teamName: String? = null
    var memberCount = 0
    var bdProviderId: Long? = null
    var bdProviderName: String? = null
    var rdProviderId: Long? = null
    var rdProviderName: String? = null
    var gender: String? = null
    var email: String? = null
    var dob: Long? = null
    var address: String? = null
    var phone: String? = null
    var countryCode: String? = null
    var hospital: String? = null
    var hospitalId: Long? = null
    var picUrl: String? = null
    private var status: Constants.PatientStatus? = null
    var joiningTime: Long? = null
    var syncTime: Long? = null
    var dischargeTime: Long? = null
    var bed: String? = null
    var note: String? = null
    private var patientCondition: Constants.PatientCondition? = null
    var oxygenSupplement: Boolean? = null
    var urgent: Boolean? = null
    var resetAcuityFlag: Boolean? = null

    //Metrics
    var docBoxPatientId: String? = null
    var docBoxManagerId: String? = null
    var timeHeartRate: Long? = null
    var timeSpO2: Long? = null


     var arterialBloodPressureSystolic: Double? = null
    var heartRate: Double? = null
    var timeRespiratoryRate: Long? = null

    //    private Double sp02;
    var respiratoryRate: Double? = null
    var arterialBloodPressureDiastolic: Double? = null
    var timeArterialBloodPressureDiastolic: Long? = null
    var timeArterialBloodPressureSystolic: Long? = null

    //    private Double fiO2;
    var timeFiO2: Long? = null
    var dobDay: Int? = null
    var dobMonth: String? = null
    var completed_by: String? = null
    var dobYear: Int? = null
    private var score: Constants.AcuityLevel? = null
    var fio2: Double? = null
    var spO2: Double? = null
    var temperature: Double? = null
    var unreadCount = 0

    constructor() {}
    constructor(
        id: Long?,
        patientId: Long?,
        text: String?,
        name: String?,
        unread: Int,
        time: Long?,
        status: Constants.PatientStatus?
    ) {
        this.id = id
        patientsId = patientId
        this.text = text
        this.name = name
        this.unread = unread
        this.time = time
        this.status = status
    }

    constructor(
        patientId: Long?,
        text: String?,
        name: String?,
        unread: Int,
        time: Long?,
        status: Constants.PatientStatus?
    ) {
        id = id
        patientsId = patientId
        this.text = text
        this.name = name
        this.unread = unread
        this.time = time
        this.status = status
    }

    fun getStatus(): Constants.PatientStatus? {
        return status
    }

    fun setStatus(status: Constants.PatientStatus?) {
        this.status = status
    }

    fun getPatientCondition(): Constants.PatientCondition? {
        return patientCondition
    }

    fun setPatientCondition(patientCondition: Constants.PatientCondition?) {
        this.patientCondition = patientCondition
    }

    fun getScore(): Constants.AcuityLevel? {
        return score
    }

    fun setScore(score: Constants.AcuityLevel?) {
        this.score = score
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val consultProvider = o as AcuityModel
        return unread == consultProvider.unread &&
                time == consultProvider.time &&
                id == consultProvider.id &&
                text == consultProvider.text &&
                msgName == consultProvider.msgName &&
                bdProviderId == consultProvider.bdProviderId &&
                bdProviderName == consultProvider.bdProviderName &&
                rdProviderId == consultProvider.rdProviderId &&
                rdProviderName == consultProvider.rdProviderName &&
                picUrl == consultProvider.picUrl && status === consultProvider.status &&
                joiningTime == consultProvider.joiningTime &&
                dischargeTime == consultProvider.dischargeTime &&
                note == consultProvider.note && patientCondition === consultProvider.patientCondition &&
                oxygenSupplement == consultProvider.oxygenSupplement &&
                resetAcuityFlag == consultProvider.resetAcuityFlag &&
                urgent == consultProvider.urgent
    }

    override fun hashCode(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.hash(
                id,
                patientsId,
                text,
                unread,
                time,
                msgName,
                bdProviderId,
                bdProviderName,
                rdProviderId,
                rdProviderName,
                picUrl,
                status,
                joiningTime,
                dischargeTime,
                note,
                patientCondition,
                oxygenSupplement,
                urgent,
                resetAcuityFlag
            )
        } else id!!.toInt()
    }

    override fun toString(): String {
        return "ConsultProvider{" +
                "Id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", Status=" + status +
                ", unread=" + unread +
                ", score=" + score +
                '}'
    }
}