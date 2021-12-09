package omnicurekotlin.example.com.patientsEndpoints.model

class Patient {

    var acceptTime: Long? = null
    var address: String? = null
    var appointmentId: Long? = null
    var arterialBloodPressureSystolic: Double? = null
    var arterialBloodPressureDiastolic: Double? = null
    var athenaDeviceId: String? = null
    var bdProviderId: Long? = null
    var bdProviderName: String? = null
    var bed: String? = null
    var countryCode: String? = null
    var dischargeMessage: String? = null
    var dischargeTime: Long? = null
    var dob: Long? = null
    var dobDay: Int? = null
    var dobMonth: String? = null
    var dobYear: Int? = null
    var docBoxManagerId: String? = null
    var docBoxId: Long? = null
    var docBoxPatientId: String? = null
    var email: String? = null
    var fname: String? = null
    var gender: String? = null
    var location: String? = null
    var wardName: String? = null
    var heartRate: Double? = null
    var temperature: Double? = null
    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var inviteTime: Long? = null
    var joiningTime: Long? = null
    var lastMessageTime: Long? = null
    var syncTime: Long? = null
    var lname: String? = null
    var name: String? = null
    var note: String? = null
    var oxygenSupplement: Boolean? = null
    var patientCondition: String? = null
    var phone: String? = null
    var picUrl: String? = null
    var rdProviderId: Long? = null
    var rdProviderName: String? = null
    var respiratoryRate: Double? = null
    var score: String? = null
    var spO2: Double? = null
    var fio2: Double? = null
    var status: String? = null
    var urgent: Boolean? = null
    var covidPositive: String? = null

    fun Patient() {}

    @JvmName("getCovidPositive1")
    fun getCovidPositive(): String? {
        return covidPositive
    }

    @JvmName("setCovidPositive1")
    fun setCovidPositive(covidPositive: String?) {
        this.covidPositive = covidPositive
    }

    @JvmName("getSyncTime1")
    fun getSyncTime(): Long? {
        return syncTime
    }

    @JvmName("setSyncTime1")
    fun setSyncTime(syncTime: Long?) {
        this.syncTime = syncTime
    }


    @JvmName("getLocation1")
    fun getLocation(): String? {
        return location
    }

    @JvmName("setLocation1")
    fun setLocation(location: String?) {
        this.location = location
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


    fun setAcceptTime(acceptTime: Long?): Patient {
        this.acceptTime = acceptTime
        return this
    }


    @JvmName("getAddress1")
    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?): Patient {
        this.address = address
        return this
    }


    @JvmName("getAppointmentId1")
    fun getAppointmentId(): Long? {
        return appointmentId
    }


    fun setAppointmentId(appointmentId: Long?): Patient {
        this.appointmentId = appointmentId
        return this
    }


    @JvmName("getArterialBloodPressureSystolic1")
    fun getArterialBloodPressureSystolic(): Double? {
        return arterialBloodPressureSystolic
    }


    fun setArterialBloodPressureSystolic(arterialBloodPressureSystolic: Double?): Patient {
        this.arterialBloodPressureSystolic = arterialBloodPressureSystolic
        return this
    }


    @JvmName("getAthenaDeviceId1")
    fun getAthenaDeviceId(): String? {
        return athenaDeviceId
    }

    fun setAthenaDeviceId(athenaDeviceId: String?):Patient {
        this.athenaDeviceId = athenaDeviceId
        return this
    }

    @JvmName("getBdProviderId1")
    fun getBdProviderId(): Long? {
        return bdProviderId
    }

    fun setBdProviderId(bdProviderId: Long?): Patient {
        this.bdProviderId = bdProviderId
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

    @JvmName("getTemperature1")
    fun getTemperature(): Double? {
        return temperature
    }

    @JvmName("setTemperature1")
    fun setTemperature(temperature: Double?) {
        this.temperature = temperature
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

    @JvmName("getDocBoxId1")
    fun getDocBoxId(): Long? {
        return docBoxId
    }

    @JvmName("setDocBoxId1")
    fun setDocBoxId(docBoxId: Long?) {
        this.docBoxId = docBoxId
    }

    @JvmName("getDischargeMessage1")
    fun getDischargeMessage(): String? {
        return dischargeMessage
    }


    fun setDischargeMessage(dischargeMessage: String?): Patient {
        this.dischargeMessage = dischargeMessage
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


    @JvmName("getDobDay1")
    fun getDobDay(): Int? {
        return dobDay
    }


    fun setDobDay(dobDay: Int?): Patient {
        this.dobDay = dobDay
        return this
    }


    @JvmName("getDobMonth1")
    fun getDobMonth(): String? {
        return dobMonth
    }


    fun setDobMonth(dobMonth: String?): Patient {
        this.dobMonth = dobMonth
        return this
    }

    @JvmName("getDobYear1")
    fun getDobYear(): Int? {
        return dobYear
    }


    fun setDobYear(dobYear: Int?):Patient {
        this.dobYear = dobYear
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


    fun setDocBoxPatientId(docBoxPatientId: String?):Patient {
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

    @JvmName("getHeartRate1")
    fun getHeartRate(): Double? {
        return heartRate
    }

    fun setHeartRate(heartRate: Double?): Patient {
        this.heartRate = heartRate
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


    @JvmName("getInviteTime1")
    fun getInviteTime(): Long? {
        return inviteTime
    }


    fun setInviteTime(inviteTime: Long?):Patient {
        this.inviteTime = inviteTime
        return this
    }


    @JvmName("getJoiningTime1")
    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long?):Patient {
        this.joiningTime = joiningTime
        return this
    }


    @JvmName("getLastMessageTime1")
    fun getLastMessageTime(): Long? {
        return lastMessageTime
    }


    fun setLastMessageTime(lastMessageTime: Long?): Patient {
        this.lastMessageTime = lastMessageTime
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


    @JvmName("getOxygenSupplement1")
    fun getOxygenSupplement(): Boolean? {
        return oxygenSupplement
    }


    fun setOxygenSupplement(oxygenSupplement: Boolean?): Patient {
        this.oxygenSupplement = oxygenSupplement
        return this
    }


    @JvmName("getPatientCondition1")
    fun getPatientCondition(): String? {
        return patientCondition
    }


    fun setPatientCondition(patientCondition: String?): Patient {
        this.patientCondition = patientCondition
        return this
    }


    @JvmName("getPhone1")
    fun getPhone(): String? {
        return phone
    }


    fun setPhone(phone: String?):Patient {
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


    @JvmName("getRdProviderId1")
    fun getRdProviderId(): Long? {
        return rdProviderId
    }


    fun setRdProviderId(rdProviderId: Long?): Patient {
        this.rdProviderId = rdProviderId
        return this
    }


    @JvmName("getRdProviderName1")
    fun getRdProviderName(): String? {
        return rdProviderName
    }


    fun setRdProviderName(rdProviderName: String?):Patient {
        this.rdProviderName = rdProviderName
        return this
    }


    @JvmName("getRespiratoryRate1")
    fun getRespiratoryRate(): Double? {
        return respiratoryRate
    }


    fun setRespiratoryRate(respiratoryRate: Double?): Patient {
        this.respiratoryRate = respiratoryRate
        return this
    }


    @JvmName("getScore1")
    fun getScore(): String? {
        return score
    }


    fun setScore(score: String?): Patient {
        this.score = score
        return this
    }


    @JvmName("getSpO21")
    fun getSpO2(): Double? {
        return spO2
    }


    fun setSpO2(spO2: Double?): Patient {
        this.spO2 = spO2
        return this
    }

    @JvmName("getArterialBloodPressureDiastolic1")
    fun getArterialBloodPressureDiastolic(): Double? {
        return arterialBloodPressureDiastolic
    }

    fun setArterialBloodPressureDiastolic(arterialBloodPressureDiastolic: Double?): Patient {
        this.arterialBloodPressureDiastolic = arterialBloodPressureDiastolic
        return this
    }

    @JvmName("getFio21")
    fun getFio2(): Double? {
        return fio2
    }

    @JvmName("setFio21")
    fun setFio2(fio2: Double?) {
        this.fio2 = fio2
    }


    @JvmName("getStatus1")
    fun getStatus(): String? {
        return status
    }


    fun setStatus(status: String?): Patient {
        this.status = status
        return this
    }


    @JvmName("getUrgent1")
    fun getUrgent(): Boolean? {
        return urgent
    }

    fun setUrgent(urgent: Boolean?): Patient {
        this.urgent = urgent
        return this
    }



}
