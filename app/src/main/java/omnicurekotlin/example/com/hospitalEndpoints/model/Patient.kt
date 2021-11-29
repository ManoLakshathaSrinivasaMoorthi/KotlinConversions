package omnicurekotlin.example.com.hospitalEndpoints.model

class Patient {


    private var address: String? = null
    private var bdProviderName: String? = null
    private var bdProviderId: String? = null
    private var bed: String? = null
    private var text: String? = null
    private var countryCode: String? = null
    private var score: String? = null
    private var dischargeTime: Long? = null
    private var dob: Long? = null
    private var docBoxManagerId: String? = null
    private var docBoxPatientId: String? = null
    private var email: String? = null
    private var completed_by: String? = null
    private var fname: String? = null
    private var gender: String? = null
    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var lname: String? = null
    private var name: String? = null
    private var note: String? = null
    private var phone: String? = null
    private var picUrl: String? = null
    private var rdProviderId: String? = null
    private var rdProviderName: String? = null
    private var status: String? = null
    private var teamName: String? = null
    private var time: Long? = null
    private var syncTime: Long? = null
    private var inviteTime: Long? = null
    private var acceptTime: Long? = null
    private var wardName: String? = null
    private var patientCondition: String? = null
    private var heartRate: Double? = null
    private var arterialBloodPressureSystolic: Double? = null
    private var arterialBloodPressureDiastolic: Double? = null
    private var spO2: Double? = null
    private var fio2: Double? = null
    private var temperature: Double? = null
    private var respiratoryRate: Double? = null
    private var heartRateValue: String? = null
    private var arterialBloodPressureSystolicValue: String? = null
    private var arterialBloodPressureDiastolicValue: String? = null
    private var spO2Value: String? = null
    private var fio2Value: String? = null
    private var temperatureValue: String? = null
    private var respiratoryRateValue: String? = null
    private var recordNumber: String? = null
    private var oxygenSupplement: Boolean? = null
    private var urgent: Boolean? = null

    fun getCompleted_by(): String? {
        return completed_by
    }

    fun setCompleted_by(completed_by: String?) {
        this.completed_by = completed_by
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getBdProviderId(): String? {
        return bdProviderId
    }

    fun setBdProviderId(bdProviderId: String?) {
        this.bdProviderId = bdProviderId
    }

    fun getRdProviderId(): String? {
        return rdProviderId
    }

    fun setRdProviderId(rdProviderId: String?) {
        this.rdProviderId = rdProviderId
    }

    fun getTeamName(): String? {
        return teamName
    }

    fun setTeamName(teamName: String?) {
        this.teamName = teamName
    }

    fun getSyncTime(): Long? {
        return syncTime
    }

    fun setSyncTime(syncTime: Long?) {
        this.syncTime = syncTime
    }

    fun getInviteTime(): Long? {
        return inviteTime
    }

    fun setInviteTime(inviteTime: Long?) {
        this.inviteTime = inviteTime
    }

    fun getWardName(): String? {
        return wardName
    }

    fun setWardName(wardName: String?) {
        this.wardName = wardName
    }

    fun getAcceptTime(): Long? {
        return acceptTime
    }

    fun setAcceptTime(acceptTime: Long?) {
        this.acceptTime = acceptTime
    }

    fun getPatientCondition(): String? {
        return patientCondition
    }

    fun setPatientCondition(patientCondition: String?) {
        this.patientCondition = patientCondition
    }

    @JvmName("getHeartRate1")
    fun getHeartRate(): Double? {
        return heartRate
    }

    @JvmName("setHeartRate1")
    fun setHeartRate(heartRate: Double?) {
        this.heartRate = heartRate
    }

    @JvmName("getArterialBloodPressureSystolic1")
    fun getArterialBloodPressureSystolic(): Double? {
        return arterialBloodPressureSystolic
    }

    @JvmName("setArterialBloodPressureSystolic1")
    fun setArterialBloodPressureSystolic(arterialBloodPressureSystolic: Double?) {
        this.arterialBloodPressureSystolic = arterialBloodPressureSystolic
    }

    @JvmName("getArterialBloodPressureDiastolic1")
    fun getArterialBloodPressureDiastolic(): Double? {
        return arterialBloodPressureDiastolic
    }

    @JvmName("setArterialBloodPressureDiastolic1")
    fun setArterialBloodPressureDiastolic(arterialBloodPressureDiastolic: Double?) {
        this.arterialBloodPressureDiastolic = arterialBloodPressureDiastolic
    }

    fun getSpO2(): Double? {
        return spO2
    }

    fun setSpO2(spO2: Double?) {
        this.spO2 = spO2
    }

    fun getFio2(): Double? {
        return fio2
    }

    fun setFio2(fio2: Double?) {
        this.fio2 = fio2
    }

    fun getRespiratoryRate(): Double? {
        return respiratoryRate
    }

    fun setRespiratoryRate(respiratoryRate: Double?) {
        this.respiratoryRate = respiratoryRate
    }

    fun getHeartRateValue(): String? {
        return heartRateValue
    }

    fun setHeartRateValue(heartRateValue: String?) {
        this.heartRateValue = heartRateValue
    }

    fun getArterialBloodPressureSystolicValue(): String? {
        return arterialBloodPressureSystolicValue
    }

    fun setArterialBloodPressureSystolicValue(arterialBloodPressureSystolicValue: String?) {
        this.arterialBloodPressureSystolicValue = arterialBloodPressureSystolicValue
    }

    fun getArterialBloodPressureDiastolicValue(): String? {
        return arterialBloodPressureDiastolicValue
    }

    fun setArterialBloodPressureDiastolicValue(arterialBloodPressureDiastolicValue: String?) {
        this.arterialBloodPressureDiastolicValue = arterialBloodPressureDiastolicValue
    }

    fun getSpO2Value(): String? {
        return spO2Value
    }

    fun setSpO2Value(spO2Value: String?) {
        this.spO2Value = spO2Value
    }

    fun getFio2Value(): String? {
        return fio2Value
    }

    fun setFio2Value(fio2Value: String?) {
        this.fio2Value = fio2Value
    }

    fun getRespiratoryRateValue(): String? {
        return respiratoryRateValue
    }

    fun setRespiratoryRateValue(respiratoryRateValue: String?) {
        this.respiratoryRateValue = respiratoryRateValue
    }

    fun getTemperature(): Double? {
        return temperature
    }

    fun setTemperature(temperature: Double?) {
        this.temperature = temperature
    }

    fun getTemperatureValue(): String? {
        return temperatureValue
    }

    fun setTemperatureValue(temperatureValue: String?) {
        this.temperatureValue = temperatureValue
    }

    fun getOxygenSupplement(): Boolean? {
        return oxygenSupplement
    }

    fun setOxygenSupplement(oxygenSupplement: Boolean?) {
        this.oxygenSupplement = oxygenSupplement
    }

    fun getUrgent(): Boolean? {
        return urgent
    }

    fun setUrgent(urgent: Boolean?) {
        this.urgent = urgent
    }

    fun getRecordNumber(): String? {
        return recordNumber
    }

    fun setRecordNumber(recordNumber: String?) {
        this.recordNumber = recordNumber
    }

    fun getTime(): Long? {
        return time
    }

    fun setTime(time: Long?) {
        this.time = time
    }


    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?): Patient {
        this.address = address
        return this
    }


    fun getBdProviderName(): String? {
        return bdProviderName
    }


    fun setBdProviderName(bdProviderName: String?): Patient {
        this.bdProviderName = bdProviderName
        return this
    }

    fun getBed(): String? {
        return bed
    }


    fun setBed(bed: String?): Patient {
        this.bed = bed
        return this
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): Patient {
        this.countryCode = countryCode
        return this
    }

    fun getDischargeTime(): Long? {
        return dischargeTime
    }

    fun setDischargeTime(dischargeTime: Long?): Patient {
        this.dischargeTime = dischargeTime
        return this
    }

    fun getDob(): Long? {
        return dob
    }


    fun setDob(dob: Long?): Patient {
        this.dob = dob
        return this
    }


    fun getDocBoxManagerId(): String? {
        return docBoxManagerId
    }


    fun setDocBoxManagerId(docBoxManagerId: String?): Patient {
        this.docBoxManagerId = docBoxManagerId
        return this
    }


    fun getDocBoxPatientId(): String? {
        return docBoxPatientId
    }


    fun setDocBoxPatientId(docBoxPatientId: String?): Patient {
        this.docBoxPatientId = docBoxPatientId
        return this
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): Patient {
        this.email = email
        return this
    }

    fun getFname(): String? {
        return fname
    }

    fun setFname(fname: String?): Patient {
        this.fname = fname
        return this
    }

    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?): Patient {
        this.gender = gender
        return this
    }

    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): Patient {
        this.hospital = hospital
        return this
    }


    fun getHospitalId(): Long? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: Long?): Patient {
        this.hospitalId = hospitalId
        return this
    }


    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): Patient {
        this.id = id
        return this
    }


    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?): Patient {
        this.joiningTime = joiningTime
        return this
    }

    fun getLname(): String? {
        return lname
    }

    fun setLname(lname: String?): Patient {
        this.lname = lname
        return this
    }

    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Patient {
        this.name = name
        return this
    }


    fun getNote(): String? {
        return note
    }


    fun setNote(note: String?): Patient {
        this.note = note
        return this
    }

    fun getPhone(): String? {
        return phone
    }


    fun setPhone(phone: String?): Patient {
        this.phone = phone
        return this
    }


    fun getPicUrl(): String? {
        return picUrl
    }

    fun setPicUrl(picUrl: String?): Patient {
        this.picUrl = picUrl
        return this
    }

    fun getRdProviderName(): String? {
        return rdProviderName
    }

    fun setRdProviderName(rdProviderName: String?): Patient {
        this.rdProviderName = rdProviderName
        return this
    }

    fun getStatus(): String? {
        return status
    }


    fun setStatus(status: String?):Patient? {
        this.status = status
        return this
    }

   /* operator fun set(fieldName: String?, value: Any?): Patient? {
        return super.set(fieldName, value) as Patient?
    }*/

   /* fun clone():  Patient? {
        return super.clone() as omnicure.mvp.com.hospitalEndpoints.model.Patient?
    }*/

    fun getScore(): String? {
        return score
    }

    fun setScore(score: String?) {
        this.score = score
    }
}

