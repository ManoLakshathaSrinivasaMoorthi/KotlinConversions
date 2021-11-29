package omnicurekotlin.example.com.userEndpoints.model

class User {
    private var access: List<String?>? = null
    private var activeHour: Float? = null
    private var address: String? = null
    private var userType: String? = null
    private var userSubType: String? = null
    private var consultationFee: Float? = null
    var countryCode: String? = null
    private var designation: String? = null
    private var remoteProviderType: String? = null
    private var dob: Long? = null
    private var email: String? = null
    private var emailOtp: String? = null
    private var emailOtpVerified: Boolean? = null
    private var experience: Float? = null
    private var fcmKey: String? = null
    private var firebaseUid: String? = null
    private var fname: String? = null
    private var gender: String? = null
    private var healthMonitoringTime: Long? = null
    private var hospital: String? = null
    private var hospitalId: Long? = null
    var id: Long? = null
    private var joiningTime: Long? = null
    private var lname: String? = null
    private var name: String? = null
    private var npiNumber: String? = null
    private var numberOfPatient: Int? = null
    private var osType: String? = null
    private var otp: String? = null
    private var password: String? = null
    var phone: String? = null
    private var profilePicUrl: String? = null
    private var qualification: String? = null
    private var role: String? = null
    private var roles: List<Role?>? = null
    private var screenName: String? = null
    private var smsOtpVerified: Boolean? = null
    private var specialization: String? = null
    private var status: String? = null
    private var token: String? = null
    private var voipToken: String? = null
    private var providerType: String? = null
    private var lcpType: String? = null
    private var remoteProviderId: Long? = null


    fun getAccess(): List<String?>? {
        return access
    }

    /**
     * @param access access or `null` for none
     */
    fun setAccess(access: List<String?>?): User {
        this.access = access
        return this
    }


    fun getActiveHour(): Float? {
        return activeHour
    }


    fun setActiveHour(activeHour: Float?): User {
        this.activeHour = activeHour
        return this
    }

    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?): User {
        this.address = address
        return this
    }


    fun getConsultationFee(): Float? {
        return consultationFee
    }

    fun setConsultationFee(consultationFee: Float?): User {
        this.consultationFee = consultationFee
        return this
    }


    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): User {
        this.countryCode = countryCode
        return this
    }

    fun getDesignation(): String? {
        return designation
    }

    fun setDesignation(designation: String?): User {
        this.designation = designation
        return this
    }

    fun getDob(): Long? {
        return dob
    }

    fun setDob(dob: Long?): User {
        this.dob = dob
        return this
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): User {
        this.email = email
        return this
    }

    fun getEmailOtp(): String? {
        return emailOtp
    }

    fun setEmailOtp(emailOtp: String?): User {
        this.emailOtp = emailOtp
        return this
    }

    fun getEmailOtpVerified(): Boolean? {
        return emailOtpVerified
    }

    fun setEmailOtpVerified(emailOtpVerified: Boolean?): User {
        this.emailOtpVerified = emailOtpVerified
        return this
    }

    fun getExperience(): Float? {
        return experience
    }


    fun setExperience(experience: Float?): User {
        this.experience = experience
        return this
    }


    fun getFcmKey(): String? {
        return fcmKey
    }

    fun setFcmKey(fcmKey: String?): User {
        this.fcmKey = fcmKey
        return this
    }

    fun getFirebaseUid(): String? {
        return firebaseUid
    }

    fun setFirebaseUid(firebaseUid: String?): User {
        this.firebaseUid = firebaseUid
        return this
    }

    fun getFname(): String? {
        return fname
    }

    fun setFname(fname: String?): User? {
        this.fname = fname
        return this
    }

    fun getUserType(): String? {
        return userType
    }

    fun setUserType(userType: String?) {
        this.userType = userType
    }

    fun getUserSubType(): String? {
        return userSubType
    }

    fun setUserSubType(userSubType: String?) {
        this.userSubType = userSubType
    }

    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?): User? {
        this.gender = gender
        return this
    }


    fun getHealthMonitoringTime(): Long? {
        return healthMonitoringTime
    }

    fun setHealthMonitoringTime(healthMonitoringTime: Long?): User? {
        this.healthMonitoringTime = healthMonitoringTime
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): User? {
        this.hospital = hospital
        return this
    }


    fun getHospitalId(): Long? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: Long?): User? {
        this.hospitalId = hospitalId
        return this
    }


    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): User {
        this.id = id
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun getRemoteProviderType(): String? {
        return remoteProviderType
    }

    fun setRemoteProviderType(remoteProviderType: String?) {
        this.remoteProviderType = remoteProviderType
    }


    fun setJoiningTime(joiningTime: Long?): User {
        this.joiningTime = joiningTime
        return this
    }


    fun getLname(): String? {
        return lname
    }


    fun setLname(lname: String?): User? {
        this.lname = lname
        return this
    }


    fun getName(): String? {
        return name
    }

    fun setName(name: String?): User? {
        this.name = name
        return this
    }


    fun getNpiNumber(): String? {
        return npiNumber
    }


    fun setNpiNumber(npiNumber: String?): User? {
        this.npiNumber = npiNumber
        return this
    }

    fun getNumberOfPatient(): Int? {
        return numberOfPatient
    }

    fun setNumberOfPatient(numberOfPatient: Int?): User? {
        this.numberOfPatient = numberOfPatient
        return this
    }


    fun getOsType(): String? {
        return osType
    }

    fun setOsType(osType: String?): User {
        this.osType = osType
        return this
    }

    fun getOtp(): String? {
        return otp
    }

    fun setOtp(otp: String?): User {
        this.otp = otp
        return this
    }


    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?): User {
        this.password = password
        return this
    }

    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String?): User? {
        this.phone = phone
        return this
    }


    fun getProfilePicUrl(): String? {
        return profilePicUrl
    }

    fun setProfilePicUrl(profilePicUrl: String?): User? {
        this.profilePicUrl = profilePicUrl
        return this
    }

    fun getQualification(): String? {
        return qualification
    }


    fun setQualification(qualification: String?): User {
        this.qualification = qualification
        return this
    }


    fun getRole(): String? {
        return role
    }

    fun setRole(role: String?): User {
        this.role = role
        return this
    }

    fun getRoles(): List<Role?>? {
        return roles
    }


    fun setRoles(roles: List<Role?>?): User? {
        this.roles = roles
        return this
    }


    fun getScreenName(): String? {
        return screenName
    }

    fun setScreenName(screenName: String?): User? {
        this.screenName = screenName
        return this
    }


    fun getSmsOtpVerified(): Boolean? {
        return smsOtpVerified
    }

    fun setSmsOtpVerified(smsOtpVerified: Boolean?): User? {
        this.smsOtpVerified = smsOtpVerified
        return this
    }


    fun getSpecialization(): String? {
        return specialization
    }

    fun setSpecialization(specialization: String?): User? {
        this.specialization = specialization
        return this
    }


    fun getStatus(): String? {
        return status
    }


    fun setStatus(status: String?): User? {
        this.status = status
        return this
    }


    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?): User? {
        this.token = token
        return this
    }


    fun getVoipToken(): String? {
        return voipToken
    }


    fun setVoipToken(voipToken: String?): User? {
        this.voipToken = voipToken
        return this
    }

    fun getProviderType(): String? {
        return providerType
    }

    fun setProviderType(providerType: String?) {
        this.providerType = providerType
    }

    fun getLcpType(): String? {
        return lcpType
    }

    fun setLcpType(lcpType: String?) {
        this.lcpType = lcpType
    }

    fun getRemoteProviderId(): Long? {
        return remoteProviderId
    }

    fun setRemoteProviderId(remoteProviderId: Long?) {
        this.remoteProviderId = remoteProviderId
    }




}
