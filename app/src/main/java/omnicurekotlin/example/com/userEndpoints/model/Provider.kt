package omnicurekotlin.example.com.userEndpoints.model

class Provider {

    private var access: List<String?>? = null
    private var activeHour: Float? = null
    private var address: String? = null
    var userType: String? = null
    var userSubType: String? = null
    private var consultationFee: Float? = null
    var countryCode: String? = null
    private var designation: String? = null
    private var remoteProviderType: String? = null
    private var dob: Long? = null
    var email: String? = null
    private var emailOtp: String? = null
    private var emailOtpVerified: Boolean? = null
    private var experience: Float? = null
    private var fcmKey: String? = null
    private var firebaseUid: String? = null
    var fname: String? = null
    private var gender: String? = null
    private var healthMonitoringTime: Long? = null
    var hospital: String? = null
    var hospitalId: Long? = null
    private var id: Long? = null
    private var joiningTime: Long? = null
    var lname: String? = null
    private var name: String? = null
    var npiNumber: String? = null
    private var numberOfPatient: Int? = null
    private var osType: String? = null
    private var otp: String? = null
    var password: String? = null
    var phone: String? = null
    private var profilePicUrl: String? = null
    private var qualification: String? = null
    var role: String? = null
    private var roles: List<Role?>? = null
    private var screenName: String? = null
    private var smsOtpVerified: Boolean? = null
    private var specialization: String? = null
    private var status: String? = null
    private var token: String? = null
    private var voipToken: String? = null
    var providerType: String? = null
    var lcpType: String? = null
    private var remoteProviderId: Long? = null


    fun getAccess(): List<String?>? {
        return access
    }

    fun setAccess(access: List<String?>?): Provider {
        this.access = access
        return this
    }

    fun getActiveHour(): Float? {
        return activeHour
    }

    fun setActiveHour(activeHour: Float?): Provider {
        this.activeHour = activeHour
        return this
    }


    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?): Provider {
        this.address = address
        return this
    }

    fun getConsultationFee(): Float? {
        return consultationFee
    }

    fun setConsultationFee(consultationFee: Float?): Provider {
        this.consultationFee = consultationFee
        return this
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): Provider {
        this.countryCode = countryCode
        return this
    }

    fun getDesignation(): String? {
        return designation
    }

    fun setDesignation(designation: String?): Provider {
        this.designation = designation
        return this
    }

    fun getDob(): Long? {
        return dob
    }

    fun setDob(dob: Long?): Provider {
        this.dob = dob
        return this
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): Provider {
        this.email = email
        return this
    }

    fun getEmailOtp(): String? {
        return emailOtp
    }

    fun setEmailOtp(emailOtp: String?): Provider {
        this.emailOtp = emailOtp
        return this
    }

    fun getEmailOtpVerified(): Boolean? {
        return emailOtpVerified
    }


    fun setEmailOtpVerified(emailOtpVerified: Boolean?): Provider {
        this.emailOtpVerified = emailOtpVerified
        return this
    }

    fun getExperience(): Float? {
        return experience
    }

    fun setExperience(experience: Float?): Provider {
        this.experience = experience
        return this
    }

    fun getFcmKey(): String? {
        return fcmKey
    }

    fun setFcmKey(fcmKey: String?): Provider {
        this.fcmKey = fcmKey
        return this
    }

    fun getFirebaseUid(): String? {
        return firebaseUid
    }

    fun setFirebaseUid(firebaseUid: String?): Provider {
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

    fun setGender(gender: String?): Provider {
        this.gender = gender
        return this
    }

    fun getHealthMonitoringTime(): Long? {
        return healthMonitoringTime
    }

    fun setHealthMonitoringTime(healthMonitoringTime: Long?): Provider {
        this.healthMonitoringTime = healthMonitoringTime
        return this
    }

    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): Provider {
        this.hospital = hospital
        return this
    }

    fun getHospitalId(): Long? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: Long?): Provider {
        this.hospitalId = hospitalId
        return this
    }

    fun getId(): Long? {
        return id
    }

    fun setId(id: Long?): Provider? {
        this.id = id
        return this
    }


    fun getJoiningTime(): Long? {
        return joiningTime
    }

    fun getRemoteProviderType(): String? {
        return remoteProviderType
    }

    fun setRemoteProviderType(remoteProviderType: String?) {
        this.remoteProviderType = remoteProviderType
    }

    fun setJoiningTime(joiningTime: Long?): Provider {
        this.joiningTime = joiningTime
        return this
    }

    fun getLname(): String? {
        return lname
    }

    fun setLname(lname: String?): Provider {
        this.lname = lname
        return this
    }

    fun getName(): String? {
        return name
    }

    /**
     * @param name name or `null` for none
     */
    fun setName(name: String?): Provider {
        this.name = name
        return this
    }

    fun getNpiNumber(): String? {
        return npiNumber
    }

    fun setNpiNumber(npiNumber: String?): Provider {
        this.npiNumber = npiNumber
        return this
    }

    fun getNumberOfPatient(): Int? {
        return numberOfPatient
    }


    fun setNumberOfPatient(numberOfPatient: Int?): Provider {
        this.numberOfPatient = numberOfPatient
        return this
    }

    fun getOsType(): String? {
        return osType
    }

    fun setOsType(osType: String?): Provider {
        this.osType = osType
        return this
    }

    fun getOtp(): String? {
        return otp
    }

    fun setOtp(otp: String?): Provider {
        this.otp = otp
        return this
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?): Provider {
        this.password = password
        return this
    }

    fun getPhone(): String? {
        return phone
    }


    fun setPhone(phone: String?): Provider {
        this.phone = phone
        return this
    }


    fun getProfilePicUrl(): String? {
        return profilePicUrl
    }

    fun setProfilePicUrl(profilePicUrl: String?): Provider {
        this.profilePicUrl = profilePicUrl
        return this
    }

    fun getQualification(): String? {
        return qualification
    }

    fun setQualification(qualification: String?): Provider {
        this.qualification = qualification
        return this
    }

    fun getRole(): String? {
        return role
    }


    fun setRole(role: String?): Provider? {
        this.role = role
        return this
    }

    fun getRoles(): List<Role?>? {
        return roles
    }
    fun setRoles(roles: List<Role?>?):Provider? {
        this.roles = roles
        return this
    }

    fun getScreenName(): String? {
        return screenName
    }

    fun setScreenName(screenName: String?): Provider? {
        this.screenName = screenName
        return this
    }

    fun getSmsOtpVerified(): Boolean? {
        return smsOtpVerified
    }
    fun setSmsOtpVerified(smsOtpVerified: Boolean?): Provider {
        this.smsOtpVerified = smsOtpVerified
        return this
    }

    fun getSpecialization(): String? {
        return specialization
    }

    fun setSpecialization(specialization: String?): Provider {
        this.specialization = specialization
        return this
    }


    fun getStatus(): String? {
        return status
    }


    fun setStatus(status: String?): Provider {
        this.status = status
        return this
    }


    fun getToken(): String? {
        return token
    }


    fun setToken(token: String?): Provider {
        this.token = token
        return this
    }


    fun getVoipToken(): String? {
        return voipToken
    }

    fun setVoipToken(voipToken: String?): Provider {
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

 /*   operator fun set(fieldName: String?, value: Any?): omnicure.mvp.com.userEndpoints.model.Provider? {
        return super.set(fieldName, value) as omnicure.mvp.com.userEndpoints.model.Provider?
    }

    fun clone(): omnicure.mvp.com.userEndpoints.model.Provider? {
        return super.clone() as omnicure.mvp.com.userEndpoints.model.Provider?
    }
*/

}

