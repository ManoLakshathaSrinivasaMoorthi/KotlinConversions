package omnicurekotlin.example.com.userEndpoints.model

class Provider {

    var access: List<String?>? = null
    var activeHour: Float? = null
    var address: String? = null
    var userType: String? = null
    var userSubType: String? = null
    var consultationFee: Float? = null
    var countryCode: String? = null
    var designation: String? = null
    var remoteProviderType: String? = null
    var dob: Long? = null
    var email: String? = null
    var emailOtp: String? = null
    var emailOtpVerified: Boolean? = null
    var experience: Float? = null
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
     var name: String? = null
    var npiNumber: String? = null
    var numberOfPatient: Int? = null
     var osType: String? = null
    var otp: String? = null
    var password: String? = null
    var phone: String? = null
    var profilePicUrl: String? = null
     var qualification: String? = null
    var role: String? = null
   var roles: List<Role?>? = null
     var screenName: String? = null
    private var smsOtpVerified: Boolean? = null
    private var specialization: String? = null
    private var status: String? = null
    private var token: String? = null
    private var voipToken: String? = null
    var providerType: String? = null
    var lcpType: String? = null
    var remoteProviderId: Long? = null


    @JvmName("getAccess1")
    fun getAccess(): List<String?>? {
        return access
    }

    fun setAccess(access: List<String?>?): Provider {
        this.access = access
        return this
    }

    @JvmName("getActiveHour1")
    fun getActiveHour(): Float? {
        return activeHour
    }

    fun setActiveHour(activeHour: Float?): Provider {
        this.activeHour = activeHour
        return this
    }

    @JvmName("getAddress1")
    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?): Provider {
        this.address = address
        return this
    }


    @JvmName("getConsultationFee1")
    fun getConsultationFee(): Float? {
        return consultationFee
    }


    fun setConsultationFee(consultationFee: Float?): Provider {
        this.consultationFee = consultationFee
        return this
    }


    @JvmName("getCountryCode1")
    fun getCountryCode(): String? {
        return countryCode
    }


    fun setCountryCode(countryCode: String?): Provider {
        this.countryCode = countryCode
        return this
    }


    @JvmName("getDesignation1")
    fun getDesignation(): String? {
        return designation
    }

    fun setDesignation(designation: String?): Provider {
        this.designation = designation
        return this
    }

    @JvmName("getDob1")
    fun getDob(): Long? {
        return dob
    }


    fun setDob(dob: Long?): Provider {
        this.dob = dob
        return this
    }

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): Provider {
        this.email = email
        return this
    }


    @JvmName("getEmailOtp1")
    fun getEmailOtp(): String? {
        return emailOtp
    }


    fun setEmailOtp(emailOtp: String?): Provider {
        this.emailOtp = emailOtp
        return this
    }

    @JvmName("getEmailOtpVerified1")
    fun getEmailOtpVerified(): Boolean? {
        return emailOtpVerified
    }


    fun setEmailOtpVerified(emailOtpVerified: Boolean?): Provider {
        this.emailOtpVerified = emailOtpVerified
        return this
    }

    @JvmName("getExperience1")
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


    @JvmName("getFname1")
    fun getFname(): String? {
        return fname
    }


    fun setFname(fname: String?): Provider {
        this.fname = fname
        return this
    }

    @JvmName("getUserType1")
    fun getUserType(): String? {
        return userType
    }

    @JvmName("setUserType1")
    fun setUserType(userType: String?) {
        this.userType = userType
    }

    @JvmName("getUserSubType1")
    fun getUserSubType(): String? {
        return userSubType
    }

    @JvmName("setUserSubType1")
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


    @JvmName("getHospital1")
    fun getHospital(): String? {
        return hospital
    }


    fun setHospital(hospital: String?): Provider {
        this.hospital = hospital
        return this
    }


    @JvmName("getHospitalId1")
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


    fun setId(id: Long?): Provider {
        this.id = id
        return this
    }


    fun getJoiningTime(): Long? {
        return joiningTime
    }

    @JvmName("getRemoteProviderType1")
    fun getRemoteProviderType(): String? {
        return remoteProviderType
    }

    @JvmName("setRemoteProviderType1")
    fun setRemoteProviderType(remoteProviderType: String?) {
        this.remoteProviderType = remoteProviderType
    }


    fun setJoiningTime(joiningTime: Long?):Provider {
        this.joiningTime = joiningTime
        return this
    }

    @JvmName("getLname1")
    fun getLname(): String? {
        return lname
    }


    fun setLname(lname: String?): Provider? {
        this.lname = lname
        return this
    }


    @JvmName("getName1")
    fun getName(): String? {
        return name
    }


    fun setName(name: String?): Provider {
        this.name = name
        return this
    }


    @JvmName("getNpiNumber1")
    fun getNpiNumber(): String? {
        return npiNumber
    }


    fun setNpiNumber(npiNumber: String?): Provider? {
        this.npiNumber = npiNumber
        return this
    }


    @JvmName("getNumberOfPatient1")
    fun getNumberOfPatient(): Int? {
        return numberOfPatient
    }

    fun setNumberOfPatient(numberOfPatient: Int?): Provider {
        this.numberOfPatient = numberOfPatient
        return this
    }



    @JvmName("getOsType1")
    fun getOsType(): String? {
        return osType
    }

    /**
     * @param osType osType or `null` for none
     */
    fun setOsType(osType: String?):Provider {
        this.osType = osType
        return this
    }


    @JvmName("getOtp1")
    fun getOtp(): String? {
        return otp
    }


    fun setOtp(otp: String?): Provider {
        this.otp = otp
        return this
    }


    @JvmName("getPassword1")
    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?): Provider {
        this.password = password
        return this
    }


    @JvmName("getPhone1")
    fun getPhone(): String? {
        return phone
    }


    fun setPhone(phone: String?): Provider {
        this.phone = phone
        return this
    }

    @JvmName("getProfilePicUrl1")
    fun getProfilePicUrl(): String? {
        return profilePicUrl
    }


    fun setProfilePicUrl(profilePicUrl: String?): Provider {
        this.profilePicUrl = profilePicUrl
        return this
    }


    @JvmName("getQualification1")
    fun getQualification(): String? {
        return qualification
    }

    fun setQualification(qualification: String?): Provider {
        this.qualification = qualification
        return this
    }

    /**
     * @return value or `null` for none
     */
    @JvmName("getRole1")
    fun getRole(): String? {
        return role
    }


    fun setRole(role: String?): Provider {
        this.role = role
        return this
    }


    @JvmName("getRoles1")
    fun getRoles(): List<Role?>? {
        return roles
    }


    fun setRoles(roles: List<Role?>?): Provider {
        this.roles = roles
        return this
    }


    @JvmName("getScreenName1")
    fun getScreenName(): String? {
        return screenName
    }


    fun setScreenName(screenName: String?): Provider {
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

    @JvmName("getProviderType1")
    fun getProviderType(): String? {
        return providerType
    }

    @JvmName("setProviderType1")
    fun setProviderType(providerType: String?) {
        this.providerType = providerType
    }

    @JvmName("getLcpType1")
    fun getLcpType(): String? {
        return lcpType
    }

    @JvmName("setLcpType1")
    fun setLcpType(lcpType: String?) {
        this.lcpType = lcpType
    }

    @JvmName("getRemoteProviderId1")
    fun getRemoteProviderId(): Long? {
        return remoteProviderId
    }

    @JvmName("setRemoteProviderId1")
    fun setRemoteProviderId(remoteProviderId: Long?) {
        this.remoteProviderId = remoteProviderId
    }

//    @Override
//    public Provider set(String fieldName, Object value) {
//        return (Provider) super.set(fieldName, value);
//    }
//
//    @Override
//    public Provider clone() {
//        return (Provider) super.clone();
//    }


}
