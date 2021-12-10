package omnicurekotlin.example.com.userEndpoints.model

class User {

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
    var fcmKey: String? = null
    var firebaseUid: String? = null
    var fname: String? = null
    var gender: String? = null
    var healthMonitoringTime: Long? = null
    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var joiningTime: Long? = null
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
    var smsOtpVerified: Boolean? = null
    var specialization: String? = null
    var status: String? = null
    var token: String? = null
    var voipToken: String? = null
    var providerType: String? = null
    var lcpType: String? = null
    var remoteProviderId: Long? = null


    @JvmName("getAccess1")
    fun getAccess(): List<String?>? {
        return access
    }


    fun setAccess(access: List<String?>?): User {
        this.access = access
        return this
    }


    @JvmName("getActiveHour1")
    fun getActiveHour(): Float? {
        return activeHour
    }


    fun setActiveHour(activeHour: Float?): User {
        this.activeHour = activeHour
        return this
    }

    @JvmName("getAddress1")
    fun getAddress(): String? {
        return address
    }


    fun setAddress(address: String?): User {
        this.address = address
        return this
    }


    @JvmName("getConsultationFee1")
    fun getConsultationFee(): Float? {
        return consultationFee
    }

    fun setConsultationFee(consultationFee: Float?): User {
        this.consultationFee = consultationFee
        return this
    }


    @JvmName("getCountryCode1")
    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?): User {
        this.countryCode = countryCode
        return this
    }

    @JvmName("getDesignation1")
    fun getDesignation(): String? {
        return designation
    }

    fun setDesignation(designation: String?): User {
        this.designation = designation
        return this
    }

    @JvmName("getDob1")
    fun getDob(): Long? {
        return dob
    }

    fun setDob(dob: Long?): User {
        this.dob = dob
        return this
    }

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): User {
        this.email = email
        return this
    }

    @JvmName("getEmailOtp1")
    fun getEmailOtp(): String? {
        return emailOtp
    }

    fun setEmailOtp(emailOtp: String?): User {
        this.emailOtp = emailOtp
        return this
    }

    @JvmName("getEmailOtpVerified1")
    fun getEmailOtpVerified(): Boolean? {
        return emailOtpVerified
    }

    fun setEmailOtpVerified(emailOtpVerified: Boolean?): User {
        this.emailOtpVerified = emailOtpVerified
        return this
    }

    @JvmName("getExperience1")
    fun getExperience(): Float? {
        return experience
    }


    fun setExperience(experience: Float?): User {
        this.experience = experience
        return this
    }


    @JvmName("getFcmKey1")
    fun getFcmKey(): String? {
        return fcmKey
    }

    fun setFcmKey(fcmKey: String?): User {
        this.fcmKey = fcmKey
        return this
    }

    @JvmName("getFirebaseUid1")
    fun getFirebaseUid(): String? {
        return firebaseUid
    }

    fun setFirebaseUid(firebaseUid: String?): User {
        this.firebaseUid = firebaseUid
        return this
    }

    @JvmName("getFname1")
    fun getFname(): String? {
        return fname
    }

    fun setFname(fname: String?): User {
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

    @JvmName("getGender1")
    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?): User {
        this.gender = gender
        return this
    }


    @JvmName("getHealthMonitoringTime1")
    fun getHealthMonitoringTime(): Long? {
        return healthMonitoringTime
    }

    fun setHealthMonitoringTime(healthMonitoringTime: Long?): User {
        this.healthMonitoringTime = healthMonitoringTime
        return this
    }


    @JvmName("getHospital1")
    fun getHospital(): String? {
        return hospital
    }

    fun setHospital(hospital: String?): User {
        this.hospital = hospital
        return this
    }


    @JvmName("getHospitalId1")
    fun getHospitalId(): Long? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: Long?): User {
        this.hospitalId = hospitalId
        return this
    }


    @JvmName("getId1")
    fun getId(): Long? {
        return id
    }


    fun setId(id: Long?): User {
        this.id = id
        return this
    }


    @JvmName("getJoiningTime1")
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


    fun setJoiningTime(joiningTime: Long?): User {
        this.joiningTime = joiningTime
        return this
    }


    @JvmName("getLname1")
    fun getLname(): String? {
        return lname
    }


    fun setLname(lname: String?): User {
        this.lname = lname
        return this
    }


    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    fun setName(name: String?): User {
        this.name = name
        return this
    }


    @JvmName("getNpiNumber1")
    fun getNpiNumber(): String? {
        return npiNumber
    }


    fun setNpiNumber(npiNumber: String?): User {
        this.npiNumber = npiNumber
        return this
    }

    @JvmName("getNumberOfPatient1")
    fun getNumberOfPatient(): Int? {
        return numberOfPatient
    }

    fun setNumberOfPatient(numberOfPatient: Int?): User {
        this.numberOfPatient = numberOfPatient
        return this
    }


    @JvmName("getOsType1")
    fun getOsType(): String? {
        return osType
    }

    fun setOsType(osType: String?): User {
        this.osType = osType
        return this
    }

    @JvmName("getOtp1")
    fun getOtp(): String? {
        return otp
    }

    fun setOtp(otp: String?): User {
        this.otp = otp
        return this
    }


    @JvmName("getPassword1")
    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?): User {
        this.password = password
        return this
    }

    @JvmName("getPhone1")
    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String?): User {
        this.phone = phone
        return this
    }


    @JvmName("getProfilePicUrl1")
    fun getProfilePicUrl(): String? {
        return profilePicUrl
    }

    fun setProfilePicUrl(profilePicUrl: String?): User {
        this.profilePicUrl = profilePicUrl
        return this
    }

    @JvmName("getQualification1")
    fun getQualification(): String? {
        return qualification
    }


    fun setQualification(qualification: String?): User {
        this.qualification = qualification
        return this
    }


    @JvmName("getRole1")
    fun getRole(): String? {
        return role
    }

    fun setRole(role: String?): User {
        this.role = role
        return this
    }

    @JvmName("getRoles1")
    fun getRoles(): List<Role?>? {
        return roles
    }


    fun setRoles(roles: List<Role?>?): User {
        this.roles = roles
        return this
    }


    @JvmName("getScreenName1")
    fun getScreenName(): String? {
        return screenName
    }

    fun setScreenName(screenName: String?): User {
        this.screenName = screenName
        return this
    }


    @JvmName("getSmsOtpVerified1")
    fun getSmsOtpVerified(): Boolean? {
        return smsOtpVerified
    }

    fun setSmsOtpVerified(smsOtpVerified: Boolean?): User {
        this.smsOtpVerified = smsOtpVerified
        return this
    }


    @JvmName("getSpecialization1")
    fun getSpecialization(): String? {
        return specialization
    }

    fun setSpecialization(specialization: String?): User {
        this.specialization = specialization
        return this
    }


    @JvmName("getStatus1")
    fun getStatus(): String? {
        return status
    }


    fun setStatus(status: String?): User {
        this.status = status
        return this
    }


    @JvmName("getToken1")
    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?): User {
        this.token = token
        return this
    }


    @JvmName("getVoipToken1")
    fun getVoipToken(): String? {
        return voipToken
    }


    fun setVoipToken(voipToken: String?): User {
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




}
