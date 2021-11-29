package omnicurekotlin.example.com.userEndpoints.model

class Patient {

    private var acceptTime: Long? = null
    private var address: String? = null
    private var appointmentId: Long? = null
    private var arterialBloodPressureSystolic: Double? = null
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
    private var docBoxPatientId: String? = null
    private var email: String? = null
    private var fname: String? = null
    private var gender: String? = null
    private var heartRate: Double? = null
    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var inviteTime: Long? = null
    private val joiningTime: Long? = null
    private var lastMessageTime: Long? = null
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
    private var status: String? = null
    private var urgent: Boolean? = null


    fun getAcceptTime(): Long? {
        return acceptTime
    }


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


    @JvmName("getArterialBloodPressureSystolic1")
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


    fun setAthenaDeviceId(athenaDeviceId: String?): Patient? {
        this.athenaDeviceId = athenaDeviceId
        return this
    }

    fun getBdProviderId(): Long? {
        return bdProviderId
    }


    fun setBdProviderId(bdProviderId: Long): Patient? {
         var bdProviderId = bdProviderId
        return this
    }


    fun getBdProviderName(): String? {
        return bdProviderName
    }


    fun setBdProviderName(bdProviderName: String?): Patient? {
        this.bdProviderName = bdProviderName
        return this
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


    fun setCountryCode(countryCode: String?):Patient? {
        this.countryCode = countryCode
        return this
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

    fun setDob(dob: Long?): Patient {
        this.dob = dob
        return this
    }


    fun getDobDay(): Int? {
        return dobDay
    }


    fun setDobDay(dobDay: Int?): Patient {
        this.dobDay = dobDay
        return this
    }


    fun getDobMonth(): String? {
        return dobMonth
    }


    fun setDobMonth(dobMonth: String?): Patient {
        this.dobMonth = dobMonth
        return this
    }


    fun getDobYear(): Int? {
        return dobYear
    }


    fun setDobYear(dobYear: Int?): Patient {
        this.dobYear = dobYear
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


    fun setDocBoxPatientId(docBoxPatientId: String?): Patient? {
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


    fun setFname(fname: String?): Patient? {
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


    fun getHeartRate(): Double? {
        return heartRate
    }

    fun setHeartRate(heartRate: Double?): Patient {
        this.heartRate = heartRate
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


    fun getInviteTime(): Long? {
        return inviteTime
    }

    fun setInviteTime(inviteTime: Long?): Patient {
        this.inviteTime = inviteTime
        return this
    }


    fun getJoiningTime(): Long? {
        return joiningTime
    }


    fun setJoiningTime(joiningTime: Long): Patient? {
       var  joiningTime = joiningTime
        return this
    }


    fun getLastMessageTime(): Long? {
        return lastMessageTime
    }


    fun setLastMessageTime(lastMessageTime: Long?): Patient? {
        this.lastMessageTime = lastMessageTime
        return this
    }

    fun getLname(): String? {
        return lname
    }


    fun setLname(lname: String?): Patient? {
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


    fun getOxygenSupplement(): Boolean? {
        return oxygenSupplement
    }

    fun setOxygenSupplement(oxygenSupplement: Boolean?): Patient {
        this.oxygenSupplement = oxygenSupplement
        return this
    }


    fun getPatientCondition(): String? {
        return patientCondition
    }


    fun setPatientCondition(patientCondition: String?): Patient {
        this.patientCondition = patientCondition
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


    fun getRdProviderId(): Long? {
        return rdProviderId
    }


    fun setRdProviderId(rdProviderId: Long?): Patient {
        this.rdProviderId = rdProviderId
        return this
    }

    fun getRdProviderName(): String? {
        return rdProviderName
    }

    fun setRdProviderName(rdProviderName: String?): Patient {
        this.rdProviderName = rdProviderName
        return this
    }


    fun getRespiratoryRate(): Double? {
        return respiratoryRate
    }


    fun setRespiratoryRate(respiratoryRate: Double?): Patient {
        this.respiratoryRate = respiratoryRate
        return this
    }

    fun getScore(): String? {
        return score
    }


    fun setScore(score: String?): Patient {
        this.score = score
        return this
    }

    fun getSpO2(): Double? {
        return spO2
    }

    fun setSpO2(spO2: Double?): Patient {
        this.spO2 = spO2
        return this
    }


    fun getStatus(): String? {
        return status
    }


    fun setStatus(status: String?): Patient {
        this.status = status
        return this
    }


    fun getUrgent(): Boolean? {
        return urgent
    }


    fun setUrgent(urgent: Boolean?): Patient {
        this.urgent = urgent
        return this
    }


}
