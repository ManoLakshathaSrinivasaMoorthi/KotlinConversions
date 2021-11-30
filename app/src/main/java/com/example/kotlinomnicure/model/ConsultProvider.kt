package com.example.kotlinomnicure.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlinomnicure.utils.Constants
import java.io.Serializable
import java.util.*

class ConsultProvider :Serializable{
    val patientsId: Long? = null

    var id: Long? = null
    var patientId: String? = null
    var text: String? = null
    var name: String? = null
    private var fname: String? = null
    private var lname: String? = null
    var unread = 0
    private var time: Long? = null
    private var msgName: String? = null
    private var wardName: String? = null
    private var recordNumber: String? = null
    private var teamName: String? = null
    private var memberCount = 0
    private var bdProviderId: Long? = null
    private var bdProviderName: String? = null
    private var rdProviderId: Long? = null
    private var rdProviderName: String? = null
    var gender: String? = null
    private var email: String? = null

    var dob: Long? = null

    private var address: String? = null

    var phone: String? = null

    private var countryCode: String? = null

    private var hospital: String? = null

    private var hospitalId: Long? = null
    private var picUrl: String? = null

    var status: Constants.PatientStatus? = null

    private var joiningTime: Long? = null

    private var syncTime: Long? = null

    private var dischargeTime: Long? = null
    private var bed: String? = null
    var note: String? = null
    var patientCondition: Constants.PatientCondition? = null
    var oxygenSupplement: Boolean? = null
    var urgent: Boolean? = null
    private var resetAcuityFlag: Boolean? = null

    //Metrics
    var docBoxPatientId: String? = null
    private var docBoxManagerId: String? = null
    private var timeHeartRate: Long? = null
    private var timeSpO2: Long? = null
    var arterialBloodPressureSystolic: Double? = null
    var heartRate: Double? = null
    private var timeRespiratoryRate: Long? = null

    //    private Double sp02;
    var respiratoryRate: Double? = null
    var arterialBloodPressureDiastolic: Double? = null
    private var timeArterialBloodPressureDiastolic: Long? = null
    private var timeArterialBloodPressureSystolic: Long? = null

    //    private Double fiO2;
    private var timeFiO2: Long? = null
    private var dobDay: Int? = null
    private var dobMonth: String? = null
    private var completed_by: String? = null
    private var dobYear: Int? = null
    var score: Constants.AcuityLevel? = null
    var fio2: Double? = null
    var spO2: Double? = null
    var temperature: Double? = null
    private var unreadCount = 0

    fun ConsultProvider() {}

    fun ConsultProvider(id: Long?, patientId: String?, text: String?, name: String?, unread: Int, time: Long?, status: Constants.PatientStatus?) {
        this.id = id
        this.patientId = patientId
        this.text = text
        this.name = name
        this.unread = unread
        this.time = time
        this.status = status
    }

    fun ConsultProvider(patientId: String?, text: String?, name: String?, unread: Int, time: Long?, status: Constants.PatientStatus?) {
        id = id
        this.patientId = patientId
        this.text = text
        this.name = name
        this.unread = unread
        this.time = time
        this.status = status
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val consultProvider = o as ConsultProvider
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
            Objects.hash(id, patientId, text, unread, time, msgName, bdProviderId,
                bdProviderName, rdProviderId, rdProviderName, picUrl, status, joiningTime,
                dischargeTime, note, patientCondition, oxygenSupplement, urgent, resetAcuityFlag)
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
