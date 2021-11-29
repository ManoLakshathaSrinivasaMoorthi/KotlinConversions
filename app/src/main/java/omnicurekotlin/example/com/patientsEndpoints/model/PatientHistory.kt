package omnicurekotlin.example.com.patientsEndpoints.model

class PatientHistory {

    private var acceptTime: Long? = null
    private var address: String? = null
    private var arterialBloodPressureSystolic: Double? = null
    private var athenaDeviceId: String? = null
    private var bdProviderId: Long? = null
    private var bdProviderName: String? = null
    private var bed: String? = null
    private var countryCode: String? = null
    private var dischargeMessage: String? = null
    private var dischargeTime: Long? = null
    private var dob: Long? = null
    private var docBoxManagerId: String? = null
    private var docBoxPatientId: String? = null
    private var email: String? = null
    private var fname: String? = null
    private var gender: String? = null
    private var heartRate: Double? = null
    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var inviteTime: Long? = null
    private var joiningTime: Long? = null
    private var lastMessageTime: Long? = null
    private var lname: String? = null
    private var name: String? = null
    private var note: String? = null
    private var oxygenSupplement: Boolean? = null
    private var patientCondition: String? = null
    private var patientId: Long? = null
    private var phone: String? = null
    private var picUrl: String? = null
    private var rdProviderId: Long? = null
    private var rdProviderName: String? = null
    private var respiratoryRate: Double? = null
    private var score: String? = null
    private var spO2: Double? = null
    private var status: String? = null
    private var urgent: Boolean? = null

    /**
     * @return value or `null` for none
     */
    fun getAcceptTime(): Long? {
        return acceptTime
    }

    /**
     * @param acceptTime acceptTime or `null` for none
     */
    fun setAcceptTime(acceptTime: Long?): PatientHistory? {
        this.acceptTime = acceptTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getAddress(): String? {
        return address
    }

    /**
     * @param address address or `null` for none
     */
    fun setAddress(address: String?): PatientHistory? {
        this.address = address
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getArterialBloodPressureSystolic(): Double? {
        return arterialBloodPressureSystolic
    }

    /**
     * @param arterialBloodPressureSystolic arterialBloodPressureSystolic or `null` for none
     */
    fun setArterialBloodPressureSystolic(arterialBloodPressureSystolic: Double?): PatientHistory? {
        this.arterialBloodPressureSystolic = arterialBloodPressureSystolic
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getAthenaDeviceId(): String? {
        return athenaDeviceId
    }

    /**
     * @param athenaDeviceId athenaDeviceId or `null` for none
     */
    fun setAthenaDeviceId(athenaDeviceId: String?): PatientHistory? {
        this.athenaDeviceId = athenaDeviceId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getBdProviderId(): Long? {
        return bdProviderId
    }

    /**
     * @param bdProviderId bdProviderId or `null` for none
     */
    fun setBdProviderId(bdProviderId: Long?): PatientHistory? {
        this.bdProviderId = bdProviderId
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getBdProviderName(): String? {
        return bdProviderName
    }

    /**
     * @param bdProviderName bdProviderName or `null` for none
     */
    fun setBdProviderName(bdProviderName: String?): PatientHistory? {
        this.bdProviderName = bdProviderName
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getBed(): String? {
        return bed
    }

    /**
     * @param bed bed or `null` for none
     */
    fun setBed(bed: String?): PatientHistory? {
        this.bed = bed
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getCountryCode(): String? {
        return countryCode
    }

    /**
     * @param countryCode countryCode or `null` for none
     */
    fun setCountryCode(countryCode: String?): PatientHistory? {
        this.countryCode = countryCode
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDischargeMessage(): String? {
        return dischargeMessage
    }

    /**
     * @param dischargeMessage dischargeMessage or `null` for none
     */
    fun setDischargeMessage(dischargeMessage: String?): PatientHistory? {
        this.dischargeMessage = dischargeMessage
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDischargeTime(): Long? {
        return dischargeTime
    }

    /**
     * @param dischargeTime dischargeTime or `null` for none
     */
    fun setDischargeTime(dischargeTime: Long?): PatientHistory? {
        this.dischargeTime = dischargeTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getDob(): Long? {
        return dob
    }

    /**
     * @param dob dob or `null` for none
     */
    fun setDob(dob: Long?): PatientHistory? {
        this.dob = dob
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
    fun setDocBoxManagerId(docBoxManagerId: String?): PatientHistory? {
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
    fun setDocBoxPatientId(docBoxPatientId: String?): PatientHistory? {
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
    fun setEmail(email: String?): PatientHistory? {
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
    fun setFname(fname: String?): PatientHistory? {
        this.fname = fname
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getGender(): String? {
        return gender
    }

    /**
     * @param gender gender or `null` for none
     */
    fun setGender(gender: String?): PatientHistory? {
        this.gender = gender
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHeartRate(): Double? {
        return heartRate
    }

    /**
     * @param heartRate heartRate or `null` for none
     */
    fun setHeartRate(heartRate: Double?): PatientHistory? {
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
    fun setHospital(hospital: String?): PatientHistory? {
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
    fun setHospitalId(hospitalId: Long?): PatientHistory? {
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
    fun setId(id: Long?): PatientHistory? {
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
    fun setInviteTime(inviteTime: Long?): PatientHistory? {
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
    fun setJoiningTime(joiningTime: Long?): PatientHistory? {
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
    fun setLastMessageTime(lastMessageTime: Long?): PatientHistory? {
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
    fun setLname(lname: String?): PatientHistory? {
        this.lname = lname
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getName(): String? {
        return name
    }

    /**
     * @param name name or `null` for none
     */
    fun setName(name: String?): PatientHistory? {
        this.name = name
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getNote(): String? {
        return note
    }

    /**
     * @param note note or `null` for none
     */
    fun setNote(note: String?): PatientHistory? {
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
    fun setOxygenSupplement(oxygenSupplement: Boolean?): PatientHistory? {
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
    fun setPatientCondition(patientCondition: String?): PatientHistory? {
        this.patientCondition = patientCondition
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPatientId(): Long? {
        return patientId
    }

    /**
     * @param patientId patientId or `null` for none
     */
    fun setPatientId(patientId: Long?): PatientHistory? {
        this.patientId = patientId
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
    fun setPhone(phone: String?): PatientHistory? {
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
    fun setPicUrl(picUrl: String?): PatientHistory? {
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
    fun setRdProviderId(rdProviderId: Long?): PatientHistory? {
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
    fun setRdProviderName(rdProviderName: String?): PatientHistory? {
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
    fun setRespiratoryRate(respiratoryRate: Double?): PatientHistory? {
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
    fun setScore(score: String?): PatientHistory? {
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
    fun setSpO2(spO2: Double?): PatientHistory? {
        this.spO2 = spO2
        return this
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
    fun setStatus(status: String?): PatientHistory? {
        this.status = status
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getUrgent(): Boolean? {
        return urgent
    }

    /**
     * @param urgent urgent or `null` for none
     */
    fun setUrgent(urgent: Boolean?): PatientHistory? {
        this.urgent = urgent
        return this
    }


}
