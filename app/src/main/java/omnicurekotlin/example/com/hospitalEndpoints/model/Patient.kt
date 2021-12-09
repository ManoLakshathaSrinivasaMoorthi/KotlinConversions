package omnicurekotlin.example.com.hospitalEndpoints.model

class Patient {


     var address: String? = null
     var bdProviderName: String? = null
     var bdProviderId: String? = null
     var bed: String? = null
     var text: String? = null
     var countryCode: String? = null
     var score: String? = null
     var dischargeTime: Long? = null
     var dob: Long? = null
     var docBoxManagerId: String? = null
     var docBoxPatientId: String? = null
     var email: String? = null
     var completed_by: String? = null
     var fname: String? = null
     var gender: String? = null
     var hospital: String? = null
     var hospitalId: Long? = null
     var id: Long? = null
     var joiningTime: Long? = null
     var lname: String? = null
     var name: String? = null
     var note: String? = null
     var phone: String? = null
     var picUrl: String? = null
     var rdProviderId: String? = null
     var rdProviderName: String? = null
     var status: String? = null
     var teamName: String? = null
     var time: Long? = null
     var syncTime: Long? = null
     var inviteTime: Long? = null
     var acceptTime: Long? = null
     var wardName: String? = null
     var patientCondition: String? = null
     var heartRate: Double? = null
     var arterialBloodPressureSystolic: Double? = null
     var arterialBloodPressureDiastolic: Double? = null
     var spO2: Double? = null
     var fio2: Double? = null
     var temperature: Double? = null
     var respiratoryRate: Double? = null
     var heartRateValue: String? = null
     var arterialBloodPressureSystolicValue: String? = null
     var arterialBloodPressureDiastolicValue: String? = null
     var spO2Value: String? = null
     var fio2Value: String? = null
     var temperatureValue: String? = null
     var respiratoryRateValue: String? = null
     var recordNumber: String? = null
     var oxygenSupplement: Boolean? = null
     var urgent: Boolean? = null

    @JvmName("getCompleted_by1")
    fun getCompleted_by(): String? {
        return completed_by
    }

    @JvmName("setCompleted_by1")
    fun setCompleted_by(completed_by: String?) {
        this.completed_by = completed_by
    }

    @JvmName("getText1")
    fun getText(): String? {
        return text
    }

    @JvmName("setText1")
    fun setText(text: String?) {
        this.text = text
    }

    @JvmName("getBdProviderId1")
    fun getBdProviderId(): String? {
        return bdProviderId
    }

    @JvmName("setBdProviderId1")
    fun setBdProviderId(bdProviderId: String?) {
        this.bdProviderId = bdProviderId
    }

    @JvmName("getRdProviderId1")
    fun getRdProviderId(): String? {
        return rdProviderId
    }

    @JvmName("setRdProviderId1")
    fun setRdProviderId(rdProviderId: String?) {
        this.rdProviderId = rdProviderId
    }

    @JvmName("getTeamName1")
    fun getTeamName(): String? {
        return teamName
    }

    @JvmName("setTeamName1")
    fun setTeamName(teamName: String?) {
        this.teamName = teamName
    }

    @JvmName("getSyncTime1")
    fun getSyncTime(): Long? {
        return syncTime
    }

    @JvmName("setSyncTime1")
    fun setSyncTime(syncTime: Long?) {
        this.syncTime = syncTime
    }

    @JvmName("getInviteTime1")
    fun getInviteTime(): Long? {
        return inviteTime
    }

    @JvmName("setInviteTime1")
    fun setInviteTime(inviteTime: Long?) {
        this.inviteTime = inviteTime
    }

    @JvmName("getWardName1")
    fun getWardName(): String? {
        return wardName
    }

    @JvmName("setWardName1")
    fun setWardName(wardName: String?) {
        this.wardName = wardName
    }

    @JvmName("getAcceptTime1")
    fun getAcceptTime(): Long? {
        return acceptTime
    }

    @JvmName("setAcceptTime1")
    fun setAcceptTime(acceptTime: Long?) {
        this.acceptTime = acceptTime
    }

    @JvmName("getPatientCondition1")
    fun getPatientCondition(): String? {
        return patientCondition
    }

    @JvmName("setPatientCondition1")
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

    @JvmName("getSpO21")
    fun getSpO2(): Double? {
        return spO2
    }

    @JvmName("setSpO21")
    fun setSpO2(spO2: Double?) {
        this.spO2 = spO2
    }

    @JvmName("getFio21")
    fun getFio2(): Double? {
        return fio2
    }

    @JvmName("setFio21")
    fun setFio2(fio2: Double?) {
        this.fio2 = fio2
    }

    @JvmName("getRespiratoryRate1")
    fun getRespiratoryRate(): Double? {
        return respiratoryRate
    }

    @JvmName("setRespiratoryRate1")
    fun setRespiratoryRate(respiratoryRate: Double?) {
        this.respiratoryRate = respiratoryRate
    }

    @JvmName("getHeartRateValue1")
    fun getHeartRateValue(): String? {
        return heartRateValue
    }

    @JvmName("setHeartRateValue1")
    fun setHeartRateValue(heartRateValue: String?) {
        this.heartRateValue = heartRateValue
    }

    @JvmName("getArterialBloodPressureSystolicValue1")
    fun getArterialBloodPressureSystolicValue(): String? {
        return arterialBloodPressureSystolicValue
    }

    @JvmName("setArterialBloodPressureSystolicValue1")
    fun setArterialBloodPressureSystolicValue(arterialBloodPressureSystolicValue: String?) {
        this.arterialBloodPressureSystolicValue = arterialBloodPressureSystolicValue
    }

    @JvmName("getArterialBloodPressureDiastolicValue1")
    fun getArterialBloodPressureDiastolicValue(): String? {
        return arterialBloodPressureDiastolicValue
    }

    @JvmName("setArterialBloodPressureDiastolicValue1")
    fun setArterialBloodPressureDiastolicValue(arterialBloodPressureDiastolicValue: String?) {
        this.arterialBloodPressureDiastolicValue = arterialBloodPressureDiastolicValue
    }

    @JvmName("getSpO2Value1")
    fun getSpO2Value(): String? {
        return spO2Value
    }

    @JvmName("setSpO2Value1")
    fun setSpO2Value(spO2Value: String?) {
        this.spO2Value = spO2Value
    }

    @JvmName("getFio2Value1")
    fun getFio2Value(): String? {
        return fio2Value
    }

    @JvmName("setFio2Value1")
    fun setFio2Value(fio2Value: String?) {
        this.fio2Value = fio2Value
    }

    @JvmName("getRespiratoryRateValue1")
    fun getRespiratoryRateValue(): String? {
        return respiratoryRateValue
    }

    @JvmName("setRespiratoryRateValue1")
    fun setRespiratoryRateValue(respiratoryRateValue: String?) {
        this.respiratoryRateValue = respiratoryRateValue
    }

    @JvmName("getTemperature1")
    fun getTemperature(): Double? {
        return temperature
    }

    @JvmName("setTemperature1")
    fun setTemperature(temperature: Double?) {
        this.temperature = temperature
    }

    @JvmName("getTemperatureValue1")
    fun getTemperatureValue(): String? {
        return temperatureValue
    }

    @JvmName("setTemperatureValue1")
    fun setTemperatureValue(temperatureValue: String?) {
        this.temperatureValue = temperatureValue
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

    @JvmName("getRecordNumber1")
    fun getRecordNumber(): String? {
        return recordNumber
    }

    @JvmName("setRecordNumber1")
    fun setRecordNumber(recordNumber: String?) {
        this.recordNumber = recordNumber
    }

    @JvmName("getTime1")
    fun getTime(): Long? {
        return time
    }

    @JvmName("setTime1")
    fun setTime(time: Long?) {
        this.time = time
    }


    @JvmName("getAddress1")
    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?): Patient {
        this.address = address
        return this
    }


    @JvmName("getBdProviderName1")
    fun getBdProviderName(): String? {
        return bdProviderName
    }


    fun setBdProviderName(bdProviderName: String?): Patient {
        this.bdProviderName = bdProviderName
        return this
    }

    @JvmName("getBed1")
    fun getBed(): String? {
        return bed
    }


    fun setBed(bed: String?): Patient {
        this.bed = bed
        return this
    }

    @JvmName("getCountryCode1")
    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): Patient {
        this.countryCode = countryCode
        return this
    }

    @JvmName("getDischargeTime1")
    fun getDischargeTime(): Long? {
        return dischargeTime
    }

    fun setDischargeTime(dischargeTime: Long?): Patient {
        this.dischargeTime = dischargeTime
        return this
    }

    @JvmName("getDob1")
    fun getDob(): Long? {
        return dob
    }


    fun setDob(dob: Long?): Patient {
        this.dob = dob
        return this
    }


    @JvmName("getDocBoxManagerId1")
    fun getDocBoxManagerId(): String? {
        return docBoxManagerId
    }


    fun setDocBoxManagerId(docBoxManagerId: String?): Patient {
        this.docBoxManagerId = docBoxManagerId
        return this
    }


    @JvmName("getDocBoxPatientId1")
    fun getDocBoxPatientId(): String? {
        return docBoxPatientId
    }


    fun setDocBoxPatientId(docBoxPatientId: String?): Patient {
        this.docBoxPatientId = docBoxPatientId
        return this
    }

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): Patient {
        this.email = email
        return this
    }

    @JvmName("getFname1")
    fun getFname(): String? {
        return fname
    }

    fun setFname(fname: String?): Patient {
        this.fname = fname
        return this
    }

    @JvmName("getGender1")
    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?): Patient {
        this.gender = gender
        return this
    }

    @JvmName("getHospital1")
    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): Patient {
        this.hospital = hospital
        return this
    }


    @JvmName("getHospitalId1")
    fun getHospitalId(): Long? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: Long?): Patient {
        this.hospitalId = hospitalId
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): Patient {
        this.id = id
        return this
    }


    @JvmName("getJoiningTime1")
    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?): Patient {
        this.joiningTime = joiningTime
        return this
    }

    @JvmName("getLname1")
    fun getLname(): String? {
        return lname
    }

    fun setLname(lname: String?): Patient {
        this.lname = lname
        return this
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Patient {
        this.name = name
        return this
    }


    @JvmName("getNote1")
    fun getNote(): String? {
        return note
    }


    fun setNote(note: String?): Patient {
        this.note = note
        return this
    }

    @JvmName("getPhone1")
    fun getPhone(): String? {
        return phone
    }


    fun setPhone(phone: String?): Patient {
        this.phone = phone
        return this
    }


    @JvmName("getPicUrl1")
    fun getPicUrl(): String? {
        return picUrl
    }

    fun setPicUrl(picUrl: String?): Patient {
        this.picUrl = picUrl
        return this
    }

    @JvmName("getRdProviderName1")
    fun getRdProviderName(): String? {
        return rdProviderName
    }

    fun setRdProviderName(rdProviderName: String?): Patient {
        this.rdProviderName = rdProviderName
        return this
    }

    @JvmName("getStatus1")
    fun getStatus(): String? {
        return status
    }


    fun setStatus(status: String?):Patient {
        this.status = status
        return this
    }


    @JvmName("getScore1")
    fun getScore(): String? {
        return score
    }

    @JvmName("setScore1")
    fun setScore(score: String?) {
        this.score = score
    }
}

