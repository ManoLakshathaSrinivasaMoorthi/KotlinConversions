
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable


class Provider : Serializable {


    var access: List<String>? = null
    var activeHour: Float? = null
    var address: String? = null
    var consultationFee: Float? = null
    var countryCode: String? = null
    var designation: String? = null
    var dob: Long? = null
    var email: String? = null
    var providerType: String? = null
    var lcpType: String? = null
    var remoteProviderId: Long? = null
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
    var userId: Long? = null
    var joiningTime: Long? = null
    var lname: String? = null
    var name: String? = null
    var remoteProviderType: String? = null
    var npiNumber: String? = null
    var numberOfPatient: Int? = null
    var osType: String? = null
    var otp: String? = null
    var password: String? = null
    var phone: String? = null
    var profilePicUrl: String? = null
    var qualification: String? = null
    var role: String? = null
    var roles: List<Role>? = null
    var screenName: String? = null
    var smsOtpVerified: Boolean? = null
    var specialization: String? = null
    var status: String? = null
    var token: String? = null
    var voipToken: String? = null


    fun setAccess(access: List<String>?): Provider {
        this.access = access
        return this
    }


    fun setActiveHour(activeHour: Float?): Provider {
        this.activeHour = activeHour
        return this
    }


    fun setAddress(address: String?): Provider {
        this.address = address
        return this
    }


    fun setConsultationFee(consultationFee: Float?): Provider {
        this.consultationFee = consultationFee
        return this
    }


    fun setCountryCode(countryCode: String?): Provider {
        this.countryCode = countryCode
        return this
    }


    fun setDesignation(designation: String?): Provider {
        this.designation = designation
        return this
    }


    fun setDob(dob: Long?): Provider {
        this.dob = dob
        return this
    }


    fun setEmail(email: String?): Provider {
        this.email = email
        return this
    }


    fun setEmailOtp(emailOtp: String?): Provider {
        this.emailOtp = emailOtp
        return this
    }


    fun setEmailOtpVerified(emailOtpVerified: Boolean?): Provider {
        this.emailOtpVerified = emailOtpVerified
        return this
    }


    fun setExperience(experience: Float?): Provider {
        this.experience = experience
        return this
    }


    fun setFcmKey(fcmKey: String?): Provider {
        this.fcmKey = fcmKey
        return this
    }


    fun setFirebaseUid(firebaseUid: String?): Provider {
        this.firebaseUid = firebaseUid
        return this
    }


    fun setFname(fname: String?): Provider {
        this.fname = fname
        return this
    }


    fun setGender(gender: String?): Provider {
        this.gender = gender
        return this
    }


    fun setHealthMonitoringTime(healthMonitoringTime: Long?): Provider {
        this.healthMonitoringTime = healthMonitoringTime
        return this
    }


    fun setHospital(hospital: String?): Provider {
        this.hospital = hospital
        return this
    }


    fun setHospitalId(hospitalId: Long?): Provider {
        this.hospitalId = hospitalId
        return this
    }


    fun setId(id: Long?): Provider {
        this.id = id
        return this
    }


    fun setUserId(userId: Long?): Provider {
        this.userId = userId
        return this
    }


    fun setJoiningTime(joiningTime: Long?): Provider {
        this.joiningTime = joiningTime
        return this
    }


    fun setLname(lname: String?): Provider {
        this.lname = lname
        return this
    }


    fun setName(name: String?): Provider {
        this.name = name
        return this
    }


    fun setNpiNumber(npiNumber: String?): Provider {
        this.npiNumber = npiNumber
        return this
    }


    fun setNumberOfPatient(numberOfPatient: Int?): Provider {
        this.numberOfPatient = numberOfPatient
        return this
    }


    fun setOsType(osType: String?): Provider {
        this.osType = osType
        return this
    }


    fun setOtp(otp: String?): Provider {
        this.otp = otp
        return this
    }


    fun setPassword(password: String?): Provider {
        this.password = password
        return this
    }

    fun setPhone(phone: String?): Provider {
        this.phone = phone
        return this
    }
    fun setProfilePicUrl(profilePicUrl: String?): Provider {
        this.profilePicUrl = profilePicUrl
        return this
    }
    fun setQualification(qualification: String?): Provider {
        this.qualification = qualification
        return this
    }

    fun setRole(role: String?): Provider {
        this.role = role
        return this
    }
    fun setRoles(roles: List<Role>?): Provider {
        this.roles = roles
        return this
    }

    fun setScreenName(screenName: String?): Provider {
        this.screenName = screenName
        return this
    }

    fun setSmsOtpVerified(smsOtpVerified: Boolean?): Provider {
        this.smsOtpVerified = smsOtpVerified
        return this
    }
    fun setSpecialization(specialization: String?): Provider {
        this.specialization = specialization
        return this
    }

    fun setStatus(status: String?): Provider {
        this.status = status
        return this
    }
    fun setToken(token: String?): Provider {
        this.token = token
        return this
    }

    fun setVoipToken(voipToken: String?): Provider {
        this.voipToken = voipToken
        return this
    }
}