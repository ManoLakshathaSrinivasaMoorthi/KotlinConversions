
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable


class Provider : Serializable {


    var access: List<String>? = null
    private var activeHour: Float? = null
    var address: String? = null
    private var consultationFee: Float? = null
    var countryCode: String? = null
    var designation: String? = null
    var dob: Long? = null
    var email: String? = null
    var providerType: String? = null
    var lcpType: String? = null
    var remoteProviderId: Long? = null
    private var emailOtp: String? = null
    private var emailOtpVerified: Boolean? = null
    private var experience: Float? = null
    var fcmKey: String? = null
    private var firebaseUid: String? = null
    var fname: String? = null
    var gender: String? = null
    var healthMonitoringTime: Long? = null
    var hospital: String? = null
    var hospitalId: Long? = null
    var id: Long? = null
    var userId: Long? = null
    var joiningTime: Long? = null
    var lname: String? = null
    var name: String? = null
    var remoteProviderType: String? = null
    private var npiNumber: String? = null
    private var numberOfPatient: Int? = null
    var osType: String? = null
    var otp: String? = null
    var password: String? = null
    var phone: String? = null
    var profilePicUrl: String? = null
    private var qualification: String? = null
    var role: String? = null
    private var roles: List<Role>? = null
    var screenName: String? = null
    private var smsOtpVerified: Boolean? = null
    private var specialization: String? = null
    var status: String? = null
    var token: String? = null
    private var voipToken: String? = null


    fun setAccess(access: List<String>?): Provider {
        this.access = access
        return this
    }

    /**
     * @param activeHour activeHour or `null` for none
     */
    fun setActiveHour(activeHour: Float?): Provider {
        this.activeHour = activeHour
        return this
    }

    /**
     * @param address address or `null` for none
     */
    fun setAddress(address: String?): Provider {
        this.address = address
        return this
    }

    /**
     * @param consultationFee consultationFee or `null` for none
     */
    fun setConsultationFee(consultationFee: Float?): Provider {
        this.consultationFee = consultationFee
        return this
    }

    /**
     * @param countryCode countryCode or `null` for none
     */
    fun setCountryCode(countryCode: String?): Provider {
        this.countryCode = countryCode
        return this
    }

    /**
     * @param designation designation or `null` for none
     */
    fun setDesignation(designation: String?): Provider {
        this.designation = designation
        return this
    }

    /**
     * @param dob dob or `null` for none
     */
    fun setDob(dob: Long?): Provider {
        this.dob = dob
        return this
    }

    /**
     * @param email email or `null` for none
     */
    fun setEmail(email: String?): Provider {
        this.email = email
        return this
    }

    /**
     * @param emailOtp emailOtp or `null` for none
     */
    fun setEmailOtp(emailOtp: String?): Provider {
        this.emailOtp = emailOtp
        return this
    }

    /**
     * @param emailOtpVerified emailOtpVerified or `null` for none
     */
    fun setEmailOtpVerified(emailOtpVerified: Boolean?): Provider {
        this.emailOtpVerified = emailOtpVerified
        return this
    }

    /**
     * @param experience experience or `null` for none
     */
    fun setExperience(experience: Float?): Provider {
        this.experience = experience
        return this
    }

    /**
     * @param fcmKey fcmKey or `null` for none
     */
    fun setFcmKey(fcmKey: String?): Provider {
        this.fcmKey = fcmKey
        return this
    }

    /**
     * @param firebaseUid firebaseUid or `null` for none
     */
    fun setFirebaseUid(firebaseUid: String?): Provider {
        this.firebaseUid = firebaseUid
        return this
    }

    /**
     * @param fname fname or `null` for none
     */
    fun setFname(fname: String?): Provider {
        this.fname = fname
        return this
    }

    /**
     * @param gender gender or `null` for none
     */
    fun setGender(gender: String?): Provider {
        this.gender = gender
        return this
    }

    /**
     * @param healthMonitoringTime healthMonitoringTime or `null` for none
     */
    fun setHealthMonitoringTime(healthMonitoringTime: Long?): Provider {
        this.healthMonitoringTime = healthMonitoringTime
        return this
    }

    /**
     * @param hospital hospital or `null` for none
     */
    fun setHospital(hospital: String?): Provider {
        this.hospital = hospital
        return this
    }

    /**
     * @param hospitalId hospitalId or `null` for none
     */
    fun setHospitalId(hospitalId: Long?): Provider {
        this.hospitalId = hospitalId
        return this
    }

    /**
     * @param id id or `null` for none
     */
    fun setId(id: Long?): Provider {
        this.id = id
        return this
    }

    /**
     * @param userId id or `null` for none
     */
    fun setUserId(userId: Long?): Provider {
        this.userId = userId
        return this
    }

    /**
     * @param joiningTime joiningTime or `null` for none
     */
    fun setJoiningTime(joiningTime: Long?): Provider {
        this.joiningTime = joiningTime
        return this
    }

    /**
     * @param lname lname or `null` for none
     */
    fun setLname(lname: String?): Provider {
        this.lname = lname
        return this
    }

    /**
     * @param name name or `null` for none
     */
    fun setName(name: String?): Provider {
        this.name = name
        return this
    }

    /**
     * @param npiNumber npiNumber or `null` for none
     */
    fun setNpiNumber(npiNumber: String?): Provider {
        this.npiNumber = npiNumber
        return this
    }

    /**
     * @param numberOfPatient numberOfPatient or `null` for none
     */
    fun setNumberOfPatient(numberOfPatient: Int?): Provider {
        this.numberOfPatient = numberOfPatient
        return this
    }

    /**
     * @param osType osType or `null` for none
     */
    fun setOsType(osType: String?): Provider {
        this.osType = osType
        return this
    }

    /**
     * @param otp otp or `null` for none
     */
    fun setOtp(otp: String?): Provider {
        this.otp = otp
        return this
    }

    /**
     * @param password password or `null` for none
     */
    fun setPassword(password: String?): Provider {
        this.password = password
        return this
    }

    /**
     * @param phone phone or `null` for none
     */
    fun setPhone(phone: String?): Provider {
        this.phone = phone
        return this
    }

    /**
     * @param profilePicUrl profilePicUrl or `null` for none
     */
    fun setProfilePicUrl(profilePicUrl: String?): Provider {
        this.profilePicUrl = profilePicUrl
        return this
    }

    /**
     * @param qualification qualification or `null` for none
     */
    fun setQualification(qualification: String?): Provider {
        this.qualification = qualification
        return this
    }

    /**
     * @param role role or `null` for none
     */
    fun setRole(role: String?): Provider {
        this.role = role
        return this
    }

    /**
     * @param roles roles or `null` for none
     */
    fun setRoles(roles: List<Role>?): Provider {
        this.roles = roles
        return this
    }

    /**
     * @param screenName screenName or `null` for none
     */
    fun setScreenName(screenName: String?): Provider {
        this.screenName = screenName
        return this
    }

    /**
     * @param smsOtpVerified smsOtpVerified or `null` for none
     */
    fun setSmsOtpVerified(smsOtpVerified: Boolean?): Provider {
        this.smsOtpVerified = smsOtpVerified
        return this
    }

    /**
     * @param specialization specialization or `null` for none
     */
    fun setSpecialization(specialization: String?): Provider {
        this.specialization = specialization
        return this
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: String?): Provider {
        this.status = status
        return this
    }

    /**
     * @param token token or `null` for none
     */
    fun setToken(token: String?): Provider {
        this.token = token
        return this
    }

    /**
     * @param voipToken voipToken or `null` for none
     */
    fun setVoipToken(voipToken: String?): Provider {
        this.voipToken = voipToken
        return this
    } //  @Override
    //  public Provider set(String fieldName, Object value) {
    //    return (Provider) super.set(fieldName, value);
    //  }
    //
    //  @Override
    //  public Provider clone() {
    //    return (Provider) super.clone();
    //  }
}