package com.example.kotlinomnicure.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlinomnicure.utils.Constants
import java.io.Serializable
import java.util.*

class ConsultProvider :Serializable{
    var patientsId: Long? = null

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
    private val fiO2: Double? = null
    private var inviteTime:Long?=null

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
    fun getInviteTime(): Long? {

        return inviteTime
    }

    fun setInviteTime(inviteTime: Long) {
        inviteTime = inviteTime
    }

    fun getWardName(): String? {
        return wardName
    }

    fun setWardName(wardName: String?) {
        this.wardName = wardName
    }

    fun getCompleted_by(): String? {
        return completed_by
    }

    fun setCompleted_by(completed_by: String?) {
        this.completed_by = completed_by
    }

    fun getUnreadCount(): Int {
        return unreadCount
    }

    fun setUnreadCount(unreadCount: Int) {
        this.unreadCount = unreadCount
    }

    fun getSyncTime(): Long? {
        return syncTime
    }

    fun setSyncTime(syncTime: Long?) {
        this.syncTime = syncTime
    }

    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: Long?) {
        this.id = id
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }

    fun getTime(): Long? {
        return time
    }

    fun setTime(time: Long?) {
        this.time = time
    }


    @JvmName("getText1")
    fun getText(): String? {
        return text
    }

    @JvmName("setText1")
    fun setText(text: String?) {
        this.text = text
    }

    @JvmName("getUnread1")
    fun getUnread(): Int {
        return unread
    }

    @JvmName("setUnread1")
    fun setUnread(unread: Int) {
        this.unread = unread
    }

    @JvmName("getPatientsId1")
    fun getPatientsId(): Long? {
        return patientsId
    }

    @JvmName("setPatientsId1")
    fun setPatientsId(patientsId: Long?) {
        this.patientsId = patientsId
    }

    fun getMsgName(): String? {
        return msgName
    }

    fun setMsgName(msgName: String?) {
        this.msgName = msgName
    }

    fun getBdProviderId(): Long? {
        return bdProviderId
    }

    fun setBdProviderId(bdProviderId: Long?) {
        this.bdProviderId = bdProviderId
    }

    fun getBdProviderName(): String? {
        return bdProviderName
    }

    fun setBdProviderName(bdProviderName: String?) {
        this.bdProviderName = bdProviderName
    }

    fun getRdProviderId(): Long? {
        return rdProviderId
    }

    fun setRdProviderId(rdProviderId: Long?) {
        this.rdProviderId = rdProviderId
    }

    fun getRdProviderName(): String? {
        return rdProviderName
    }

    fun setRdProviderName(rdProviderName: String?) {
        this.rdProviderName = rdProviderName
    }

    @JvmName("getGender1")
    fun getGender(): String? {
        return gender
    }

    @JvmName("setGender1")
    fun setGender(gender: String?) {
        this.gender = gender
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    @JvmName("getDob1")
    fun getDob(): Long? {
        return dob
    }

    @JvmName("setDob1")
    fun setDob(dob: Long?) {
        this.dob = dob
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    @JvmName("getPhone1")
    fun getPhone(): String? {
        return phone
    }

    @JvmName("setPhone1")
    fun setPhone(phone: String?) {
        this.phone = phone
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?) {
        this.hospital = hospital
    }

    fun getHospitalId(): Long? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: Long?) {
        this.hospitalId = hospitalId
    }

    fun getPicUrl(): String? {
        return picUrl
    }

    fun setPicUrl(picUrl: String?) {
        this.picUrl = picUrl
    }

    @JvmName("getStatus1")
    fun getStatus(): Constants.PatientStatus? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: Constants.PatientStatus?) {
        this.status = status
    }

    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun setJoiningTime(joiningTime: Long?) {
        this.joiningTime = joiningTime
    }

    fun getDischargeTime(): Long? {
        return dischargeTime
    }

    fun setDischargeTime(dischargeTime: Long?) {
        this.dischargeTime = dischargeTime
    }

    fun getBed(): String? {
        return bed
    }

    fun setBed(bed: String?) {
        this.bed = bed
    }

    fun getMemberCount(): Int {
        return memberCount
    }

    fun setMemberCount(memberCount: Int) {
        this.memberCount = memberCount
    }

    @JvmName("getNote1")
    fun getNote(): String? {
        return note
    }

    @JvmName("setNote1")
    fun setNote(note: String?) {
        this.note = note
    }

    @JvmName("getDocBoxPatientId1")
    fun getDocBoxPatientId(): String? {
        return docBoxPatientId
    }

    @JvmName("setDocBoxPatientId1")
    fun setDocBoxPatientId(docBoxPatientId: String?) {
        this.docBoxPatientId = docBoxPatientId
    }

    fun getDocBoxManagerId(): String? {
        return docBoxManagerId
    }

    fun setDocBoxManagerId(docBoxManagerId: String?) {
        this.docBoxManagerId = docBoxManagerId
    }

    fun getTimeHeartRate(): Long? {
        return timeHeartRate
    }

    fun setTimeHeartRate(timeHeartRate: Long?) {
        this.timeHeartRate = timeHeartRate
    }

    fun getTimeSpO2(): Long? {
        return timeSpO2
    }

    fun setTimeSpO2(timeSpO2: Long?) {
        this.timeSpO2 = timeSpO2
    }

    fun getTimeRespiratoryRate(): Long? {
        return timeRespiratoryRate
    }

    fun setTimeRespiratoryRate(timeRespiratoryRate: Long?) {
        this.timeRespiratoryRate = timeRespiratoryRate
    }

    @JvmName("getArterialBloodPressureSystolic1")
    fun getArterialBloodPressureSystolic(): Double? {
        return arterialBloodPressureSystolic
    }


    @JvmName("setArterialBloodPressureSystolic1")
    fun setArterialBloodPressureSystolic(arterialBloodPressureSystolic: Double?) {
        this.arterialBloodPressureSystolic = arterialBloodPressureSystolic
    }

    @JvmName("getHeartRate1")
    fun getHeartRate(): Double? {
        return heartRate
    }

    @JvmName("setHeartRate1")
    fun setHeartRate(heartRate: Double?) {
        this.heartRate = heartRate
    }

    @JvmName("getRespiratoryRate1")
    fun getRespiratoryRate(): Double? {
        return respiratoryRate
    }

    @JvmName("setRespiratoryRate1")
    fun setRespiratoryRate(respiratoryRate: Double?) {
        this.respiratoryRate = respiratoryRate
    }

    @JvmName("getArterialBloodPressureDiastolic1")
    fun getArterialBloodPressureDiastolic(): Double? {
        return arterialBloodPressureDiastolic
    }

    @JvmName("setArterialBloodPressureDiastolic1")
    fun setArterialBloodPressureDiastolic(arterialBloodPressureDiastolic: Double?) {
        this.arterialBloodPressureDiastolic = arterialBloodPressureDiastolic
    }

    fun getFiO2(): Double? {
        return fiO2
    }

    fun setFiO2(fiO2: Double) {
        fiO2 = fiO2
    }

    @JvmName("getSpO21")
    fun getSpO2(): Double? {
        return spO2
    }

    @JvmName("setSpO21")
    fun setSpO2(spO2: Double?) {
        this.spO2 = spO2
    }

    fun getTimeArterialBloodPressureDiastolic(): Long? {
        return timeArterialBloodPressureDiastolic
    }

    fun setTimeArterialBloodPressureDiastolic(timeArterialBloodPressureDiastolic: Long?) {
        this.timeArterialBloodPressureDiastolic = timeArterialBloodPressureDiastolic
    }

    fun getTimeArterialBloodPressureSystolic(): Long? {
        return timeArterialBloodPressureSystolic
    }

    fun setTimeArterialBloodPressureSystolic(timeArterialBloodPressureSystolic: Long?) {
        this.timeArterialBloodPressureSystolic = timeArterialBloodPressureSystolic
    }

    fun getRecordNumber(): String? {
        return recordNumber
    }

    fun setRecordNumber(recordNumber: String?) {
        this.recordNumber = recordNumber
    }

    fun getFname(): String? {
        return fname
    }

    fun setFname(fname: String?) {
        this.fname = fname
    }

    fun getLname(): String? {
        return lname
    }

    fun setLname(lname: String?) {
        this.lname = lname
    }

    @JvmName("getTemperature1")
    fun getTemperature(): Double? {
        return temperature
    }

    @JvmName("setTemperature1")
    fun setTemperature(temperature: Double?) {
        this.temperature = temperature
    }


    fun getTimeFiO2(): Long? {
        return timeFiO2
    }

    fun setTimeFiO2(timeFiO2: Long?) {
        this.timeFiO2 = timeFiO2
    }

    @JvmName("getPatientCondition1")
    fun getPatientCondition(): Constants.PatientCondition? {
        return patientCondition
    }

    fun getTeamName(): String? {
        return teamName
    }

    fun setTeamName(teamName: String?) {
        this.teamName = teamName
    }

    @JvmName("setPatientCondition1")
    fun setPatientCondition(patientCondition: Constants.PatientCondition?) {
        this.patientCondition = patientCondition
    }

    @JvmName("getOxygenSupplement1")
    fun getOxygenSupplement(): Boolean? {
        return oxygenSupplement
    }

    @JvmName("setOxygenSupplement1")
    fun setOxygenSupplement(oxygenSupplement: Boolean?) {
        this.oxygenSupplement = oxygenSupplement
    }

    @JvmName("getUrgent1")
    fun getUrgent(): Boolean? {
        return urgent
    }

    @JvmName("setUrgent1")
    fun setUrgent(urgent: Boolean?) {
        this.urgent = urgent
    }

    fun getResetAcuityFlag(): Boolean? {
        return resetAcuityFlag
    }

    fun setResetAcuityFlag(resetAcuityFlag: Boolean?) {
        this.resetAcuityFlag = resetAcuityFlag
    }

    fun getDobDay(): Int? {
        return dobDay
    }

    fun setDobDay(dobDay: Int?) {
        this.dobDay = dobDay
    }

    fun getDobMonth(): String? {
        return dobMonth
    }

    fun setDobMonth(dobMonth: String?) {
        this.dobMonth = dobMonth
    }

    fun getDobYear(): Int? {
        return dobYear
    }

    fun setDobYear(dobYear: Int?) {
        this.dobYear = dobYear
    }


    @JvmName("getScore1")
    fun getScore(): Constants.AcuityLevel? {
        return score
    }

    @JvmName("setScore1")
    fun setScore(score: Constants.AcuityLevel?) {
        this.score = score
    }

    @JvmName("getPatientId1")
    fun getPatientId(): String? {
        return patientId
    }

    fun setPatientId(patientId: Long?) {
        this.patientId = patientId.toString()
    }

}
