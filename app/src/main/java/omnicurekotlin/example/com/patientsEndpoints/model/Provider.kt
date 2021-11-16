package omnicurekotlin.example.com.patientsEndpoints.model

class Provider {

    private var access: List<String?>? = null
    private var activeHour: Float? = null
    private var address: String? = null
    private var consultationFee: Float? = null
    private var countryCode: String? = null
    private var designation: String? = null
    private var dob: Long? = null
    private var email: String? = null
    private var experience: Float? = null
    private var fcmKey: String? = null
    private var firebaseUid: String? = null
    private var fname: String? = null
    private var gender: String? = null
    private var healthMonitoringTime: Long? = null
    private var hospital: String? = null
    private var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    private var lname: String? = null
    private var name: String? = null
    private var npiNumber: String? = null
    private var numberOfPatient: Int? = null
    private var osType: String? = null
    private var otp: String? = null
    private var password: String? = null
    private var phone: String? = null
    private var profilePicUrl: String? = null
    private var qualification: String? = null
    private var role: String? = null
    private var roles: List<Role?>? = null
    private var screenName: String? = null
    private var specialization: String? = null

    private var status: String? = null


    private var token: String? = null

    private var voipToken: String? = null


    fun getAccess(): List<String?>? {
        return access
    }


    fun setAccess(access: List<String?>?):Provider? {
        this.access = access
        return this
    }


    fun getActiveHour(): Float? {
        return activeHour
    }


    fun setActiveHour(activeHour: Float?):Provider? {
        this.activeHour = activeHour
        return this
    }


    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?):Provider? {
        this.address = address
        return this
    }


    fun getConsultationFee(): Float? {
        return consultationFee
    }


    fun setConsultationFee(consultationFee: Float?): Provider? {
        this.consultationFee = consultationFee
        return this
    }


    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): Provider? {
        this.countryCode = countryCode
        return this
    }


    fun getDesignation(): String? {
        return designation
    }


    fun setDesignation(designation: String?): Provider? {
        this.designation = designation
        return this
    }


    fun getDob(): Long? {
        return dob
    }


    fun setDob(dob: Long?): Provider? {
        this.dob = dob
        return this
    }


    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?):Provider? {
        this.email = email
        return this
    }


    fun getExperience(): Float? {
        return experience
    }


    fun setExperience(experience: Float?): Provider? {
        this.experience = experience
        return this
    }


    fun getFcmKey(): String? {
        return fcmKey
    }


    fun setFcmKey(fcmKey: String?): Provider? {
        this.fcmKey = fcmKey
        return this
    }


    fun getFirebaseUid(): String? {
        return firebaseUid
    }


    fun setFirebaseUid(firebaseUid: String?): Provider? {
        this.firebaseUid = firebaseUid
        return this
    }


    fun getFname(): String? {
        return fname
    }


    fun setFname(fname: String?): Provider? {
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
    fun setGender(gender: String?):Provider? {
        this.gender = gender
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHealthMonitoringTime(): Long? {
        return healthMonitoringTime
    }

    /**
     * @param healthMonitoringTime healthMonitoringTime or `null` for none
     */
    fun setHealthMonitoringTime(healthMonitoringTime: Long?): Provider? {
        this.healthMonitoringTime = healthMonitoringTime
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
    fun setHospital(hospital: String?): Provider? {
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
    fun setHospitalId(hospitalId: Long?): Provider? {
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
    fun setId(id: Long?): Provider? {
        this.id = id
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
    fun setJoiningTime(joiningTime: Long?): Provider? {
        this.joiningTime = joiningTime
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
    fun setLname(lname: String?): Provider? {
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
    fun setName(name: String?): Provider? {
        this.name = name
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getNpiNumber(): String? {
        return npiNumber
    }

    /**
     * @param npiNumber npiNumber or `null` for none
     */
    fun setNpiNumber(npiNumber: String?): Provider? {
        this.npiNumber = npiNumber
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getNumberOfPatient(): Int? {
        return numberOfPatient
    }

    /**
     * @param numberOfPatient numberOfPatient or `null` for none
     */
    fun setNumberOfPatient(numberOfPatient: Int?): Provider? {
        this.numberOfPatient = numberOfPatient
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getOsType(): String? {
        return osType
    }

    /**
     * @param osType osType or `null` for none
     */
    fun setOsType(osType: String?): Provider? {
        this.osType = osType
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getOtp(): String? {
        return otp
    }

    /**
     * @param otp otp or `null` for none
     */
    fun setOtp(otp: String?): Provider? {
        this.otp = otp
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getPassword(): String? {
        return password
    }


    fun setPassword(password: String?): Provider? {
        this.password = password
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
    fun setPhone(phone: String?): Provider? {
        this.phone = phone
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getProfilePicUrl(): String? {
        return profilePicUrl
    }

    /**
     * @param profilePicUrl profilePicUrl or `null` for none
     */
    fun setProfilePicUrl(profilePicUrl: String?): Provider? {
        this.profilePicUrl = profilePicUrl
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getQualification(): String? {
        return qualification
    }

    /**
     * @param qualification qualification or `null` for none
     */
    fun setQualification(qualification: String?): Provider? {
        this.qualification = qualification
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRole(): String? {
        return role
    }

    /**
     * @param role role or `null` for none
     */
    fun setRole(role: String?):Provider? {
        this.role = role
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getRoles(): List<Role?>? {
        return roles
    }

    /**
     * @param roles roles or `null` for none
     */
    fun setRoles(roles: List<Role?>?): Provider? {
        this.roles = roles
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getScreenName(): String? {
        return screenName
    }

    /**
     * @param screenName screenName or `null` for none
     */
    fun setScreenName(screenName: String?): Provider? {
        this.screenName = screenName
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getSpecialization(): String? {
        return specialization
    }

    /**
     * @param specialization specialization or `null` for none
     */
    fun setSpecialization(specialization: String?): Provider? {
        this.specialization = specialization
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
    fun setStatus(status: String?):Provider? {
        this.status = status
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getToken(): String? {
        return token
    }

    /**
     * @param token token or `null` for none
     */
    fun setToken(token: String?):Provider? {
        this.token = token
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getVoipToken(): String? {
        return voipToken
    }

    /**
     * @param voipToken voipToken or `null` for none
     */
    fun setVoipToken(voipToken: String?): Provider? {
        this.voipToken = voipToken
        return this
    }



}
