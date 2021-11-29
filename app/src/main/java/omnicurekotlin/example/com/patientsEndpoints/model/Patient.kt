package omnicurekotlin.example.com.patientsEndpoints.model

class Patient {


    private var acceptTime: Long? = null
    private var address: String? = null
    private var appointmentId: Long? = null
    private var arterialBloodPressureSystolic: Double? = null
    private var arterialBloodPressureDiastolic: Double? = null
    private var athenaDeviceId: String? = null
    private var bdProviderId: Long? = null
    private var bdProviderName: String? = null
    private var bed: String? = null
    private var countryCode: String? = null
    private var dischargeMessage: String? = null
    private var dischargeTime: Long? = null
    private var dob: Long? = null
    private var dobDay: Int? = null
    private var dobMonth: String? = null

    private var dobYear: Int? = null
    private var docBoxManagerId: String? = null
    private var docBoxId: Long? = null
    private var docBoxPatientId: String? = null
    private var email: String? = null
    private var fname: String? = null
    private var gender: String? = null
    private var location: String? = null
    private var wardName: String? = null
    private var heartRate: Double? = null
    private var temperature: Double? = null
    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var inviteTime: Long? = null
    private var joiningTime: Long? = null
    private var lastMessageTime: Long? = null
    private var syncTime: Long? = null
    private var lname: String? = null
    private var name: String? = null
    private var note: String? = null
    private var oxygenSupplement: Boolean? = null
    private var patientCondition: String? = null
    private var phone: String? = null
    private var picUrl: String? = null
    private var rdProviderId: Long? = null
    private var rdProviderName: String? = null
    private var respiratoryRate: Double? = null
    private var score: String? = null
    private var spO2: Double? = null
    private var fio2: Double? = null
    private var status: String? = null
    private var urgent: Boolean? = null
    private var covidPositive: String? = null

    fun Patient() {}

    fun getCovidPositive(): String? {
        return covidPositive
    }

    fun setCovidPositive(covidPositive: String?) {
        this.covidPositive = covidPositive
    }

    fun getSyncTime(): Long? {
        return syncTime
    }

    fun setSyncTime(syncTime: Long?) {
        this.syncTime = syncTime
    }


    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getWardName(): String? {
        return wardName
    }

    fun setWardName(wardName: String?) {
        this.wardName = wardName
    }

    /**
     * @return value or `null` for none
     */
    fun getAcceptTime(): Long? {
        return acceptTime
    }

    /**
     * @param acceptTime acceptTime or `null` for none
     */
    fun setAcceptTime(acceptTime: Long?): Patient? {
        this.acceptTime = acceptTime
        return this
    }


    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?): Patient? {
        this.address = address
        return this
    }


    fun getAppointmentId(): Long? {
        return appointmentId
    }


    fun setAppointmentId(appointmentId: Long?): Patient? {
        this.appointmentId = appointmentId
        return this
    }


    fun getArterialBloodPressureSystolic(): Double? {
        return arterialBloodPressureSystolic
    }


    fun setArterialBloodPressureSystolic(arterialBloodPressureSystolic: Double?): Patient? {
        this.arterialBloodPressureSystolic = arterialBloodPressureSystolic
        return this
    }


    fun getAthenaDeviceId(): String? {
        return athenaDeviceId
    }

    fun setAthenaDeviceId(athenaDeviceId: String?):Patient? {
        this.athenaDeviceId = athenaDeviceId
        return this
    }

    fun getBdProviderId(): Long? {
        return bdProviderId
    }

    fun setBdProviderId(bdProviderId: Long?): Patient? {
        this.bdProviderId = bdProviderId
        return this
    }


    fun getBdProviderName(): String? {
        return bdProviderName
    }


    fun setBdProviderName(bdProviderName: String?): Patient? {
        this.bdProviderName = bdProviderName
        return this
    }

    fun getTemperature(): Double? {
        return temperature
    }

    fun setTemperature(temperature: Double?) {
        this.temperature = temperature
    }


    fun getBed(): String? {
        return bed
    }


    fun setBed(bed: String?): Patient? {
        this.bed = bed
        return this
    }


    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): Patient? {
        this.countryCode = countryCode
        return this
    }

    fun getDocBoxId(): Long? {
        return docBoxId
    }

    fun setDocBoxId(docBoxId: Long?) {
        this.docBoxId = docBoxId
    }

    fun getDischargeMessage(): String? {
        return dischargeMessage
    }


    fun setDischargeMessage(dischargeMessage: String?): Patient? {
        this.dischargeMessage = dischargeMessage
        return this
    }


    fun getDischargeTime(): Long? {
        return dischargeTime
    }

    fun setDischargeTime(dischargeTime: Long?): Patient? {
        this.dischargeTime = dischargeTime
        return this
    }


    fun getDob(): Long? {
        return dob
    }


    fun setDob(dob: Long?): Patient? {
        this.dob = dob
        return this
    }


    fun getDobDay(): Int? {
        return dobDay
    }


    fun setDobDay(dobDay: Int?): Patient? {
        this.dobDay = dobDay
        return this
    }


    fun getDobMonth(): String? {
        return dobMonth
    }


    fun setDobMonth(dobMonth: String?): Patient? {
        this.dobMonth = dobMonth
        return this
    }

    fun getDobYear(): Int? {
        return dobYear
    }

    /**
     * @param dobYear dobYear or `null` for none
     */
    fun setDobYear(dobYear: Int?):Patient? {
        this.dobYear = dobYear
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDocBoxManagerId(): String? {
        return docBoxManagerId
    }

    /**
     * @param docBoxManagerId docBoxManagerId or `null` for none
     */
    fun setDocBoxManagerId(docBoxManagerId: String?): Patient? {
        this.docBoxManagerId = docBoxManagerId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDocBoxPatientId(): String? {
        return docBoxPatientId
    }

    /**
     * @param docBoxPatientId docBoxPatientId or `null` for none
     */
    fun setDocBoxPatientId(docBoxPatientId: String?):Patient? {
        this.docBoxPatientId = docBoxPatientId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getEmail(): String? {
        return email
    }

    /**
     * @param email email or `null` for none
     */
    fun setEmail(email: String?): Patient? {
        this.email = email
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getFname(): String? {
        return fname
    }

    /**
     * @param fname fname or `null` for none
     */
    fun setFname(fname: String?): Patient? {
        this.fname = fname
        return this
    }


    fun getGender(): String? {
        return gender
    }


    fun setGender(gender: String?): Patient? {
        this.gender = gender
        return this
    }

    fun getHeartRate(): Double? {
        return heartRate
    }

    fun setHeartRate(heartRate: Double?): Patient? {
        this.heartRate = heartRate
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHospital(): String? {
        return hospital
    }

    /**
     * @param hospital hospital or `null` for none
     */
    fun setHospital(hospital: String?): Patient? {
        this.hospital = hospital
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHospitalId(): Long? {
        return hospitalId
    }

    /**
     * @param hospitalId hospitalId or `null` for none
     */
    fun setHospitalId(hospitalId: Long?): Patient? {
        this.hospitalId = hospitalId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getId(): Long? {
        return id
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): Patient? {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getInviteTime(): Long? {
        return inviteTime
    }

    /**
     * @param inviteTime inviteTime or `null` for none
     */
    fun setInviteTime(inviteTime: Long?):Patient? {
        this.inviteTime = inviteTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getJoiningTime(): Long? {
        return joiningTime
    }

    /**
     * @param joiningTime joiningTime or `null` for none
     */
    fun setJoiningTime(joiningTime: Long?):Patient? {
        this.joiningTime = joiningTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getLastMessageTime(): Long? {
        return lastMessageTime
    }

    /**
     * @param lastMessageTime lastMessageTime or `null` for none
     */
    fun setLastMessageTime(lastMessageTime: Long?): Patient? {
        this.lastMessageTime = lastMessageTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getLname(): String? {
        return lname
    }

    /**
     * @param lname lname or `null` for none
     */
    fun setLname(lname: String?): Patient? {
        this.lname = lname
        return this
    }


    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Patient? {
        this.name = name
        return this
    }


    fun getNote(): String? {
        return note
    }

    /**
     * @param note note or `null` for none
     */
    fun setNote(note: String?): Patient? {
        this.note = note
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getOxygenSupplement(): Boolean? {
        return oxygenSupplement
    }

    /**
     * @param oxygenSupplement oxygenSupplement or `null` for none
     */
    fun setOxygenSupplement(oxygenSupplement: Boolean?): Patient? {
        this.oxygenSupplement = oxygenSupplement
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPatientCondition(): String? {
        return patientCondition
    }

    /**
     * @param patientCondition patientCondition or `null` for none
     */
    fun setPatientCondition(patientCondition: String?): Patient? {
        this.patientCondition = patientCondition
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPhone(): String? {
        return phone
    }

    /**
     * @param phone phone or `null` for none
     */
    fun setPhone(phone: String?):Patient? {
        this.phone = phone
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPicUrl(): String? {
        return picUrl
    }

    /**
     * @param picUrl picUrl or `null` for none
     */
    fun setPicUrl(picUrl: String?): Patient? {
        this.picUrl = picUrl
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRdProviderId(): Long? {
        return rdProviderId
    }

    /**
     * @param rdProviderId rdProviderId or `null` for none
     */
    fun setRdProviderId(rdProviderId: Long?): Patient? {
        this.rdProviderId = rdProviderId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRdProviderName(): String? {
        return rdProviderName
    }

    /**
     * @param rdProviderName rdProviderName or `null` for none
     */
    fun setRdProviderName(rdProviderName: String?):Patient? {
        this.rdProviderName = rdProviderName
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRespiratoryRate(): Double? {
        return respiratoryRate
    }

    /**
     * @param respiratoryRate respiratoryRate or `null` for none
     */
    fun setRespiratoryRate(respiratoryRate: Double?): Patient? {
        this.respiratoryRate = respiratoryRate
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getScore(): String? {
        return score
    }

    /**
     * @param score score or `null` for none
     */
    fun setScore(score: String?): Patient? {
        this.score = score
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getSpO2(): Double? {
        return spO2
    }

    /**
     * @param spO2 spO2 or `null` for none
     */
    fun setSpO2(spO2: Double?): Patient? {
        this.spO2 = spO2
        return this
    }

    fun getArterialBloodPressureDiastolic(): Double? {
        return arterialBloodPressureDiastolic
    }

    fun setArterialBloodPressureDiastolic(arterialBloodPressureDiastolic: Double?):Patient? {
        this.arterialBloodPressureDiastolic = arterialBloodPressureDiastolic
        return this
    }

    fun getFio2(): Double? {
        return fio2
    }

    fun setFio2(fio2: Double?) {
        this.fio2 = fio2
    }

    /**
     * @return value or `null` for none
     */
    fun getStatus(): String? {
        return status
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: String?): Patient? {
        this.status = status
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getUrgent(): Boolean? {
        return urgent
    }

    fun setUrgent(urgent: Boolean?): Patient? {
        this.urgent = urgent
        return this
    }



}
