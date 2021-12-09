package omnicurekotlin.example.com.providerEndpoints.model

class ContactAdminParams {

     var message: String? = null
     var providerId: Long? = null
     var userType: String? = null
     var userDevice: String? = null
     var appVersion: String? = null
     var hospitalName: String? = null
     var subUserType: String? = null
     var email: String? = null
     var userName: String? = null


    @JvmName("getUserName1")
    fun getUserName(): String? {
        return userName
    }

    @JvmName("setUserName1")
    fun setUserName(userName: String?) {
        this.userName = userName
    }

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    @JvmName("setEmail1")
    fun setEmail(email: String?) {
        this.email = email
    }

    @JvmName("getProviderId1")
    fun getProviderId(): Long? {
        return providerId
    }

    @JvmName("setProviderId1")
    fun setProviderId(providerId: Long?) {
        this.providerId = providerId
    }

    @JvmName("getHospitalName1")
    fun getHospitalName(): String? {
        return hospitalName
    }

    @JvmName("setHospitalName1")
    fun setHospitalName(hospitalName: String?) {
        this.hospitalName = hospitalName
    }

    @JvmName("getSubUserType1")
    fun getSubUserType(): String? {
        return subUserType
    }

    @JvmName("setSubUserType1")
    fun setSubUserType(subUserType: String?) {
        this.subUserType = subUserType
    }

    @JvmName("getMessage1")
    fun getMessage(): String? {
        return message
    }

    @JvmName("setMessage1")
    fun setMessage(message: String?) {
        this.message = message
    }

    @JvmName("getUserType1")
    fun getUserType(): String? {
        return userType
    }

    @JvmName("setUserType1")
    fun setUserType(userType: String?) {
        this.userType = userType
    }

    @JvmName("getUserDevice1")
    fun getUserDevice(): String? {
        return userDevice
    }

    @JvmName("setUserDevice1")
    fun setUserDevice(userDevice: String?) {
        this.userDevice = userDevice
    }

    @JvmName("getAppVersion1")
    fun getAppVersion(): String? {
        return appVersion
    }

    @JvmName("setAppVersion1")
    fun setAppVersion(appVersion: String?) {
        this.appVersion = appVersion
    }
}
