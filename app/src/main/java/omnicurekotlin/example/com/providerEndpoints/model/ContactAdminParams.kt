package omnicurekotlin.example.com.providerEndpoints.model

class ContactAdminParams {

    private var message: String? = null
    private var providerId: Long? = null
    private var userType: String? = null
    private var userDevice: String? = null
    private var appVersion: String? = null
    private var hospitalName: String? = null
    private var subUserType: String? = null

    fun getProviderId(): Long? {
        return providerId
    }

    fun setProviderId(providerId: Long?) {
        this.providerId = providerId
    }

    fun getHospitalName(): String? {
        return hospitalName
    }

    fun setHospitalName(hospitalName: String?) {
        this.hospitalName = hospitalName
    }

    fun getSubUserType(): String? {
        return subUserType
    }

    fun setSubUserType(subUserType: String?) {
        this.subUserType = subUserType
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getUserType(): String? {
        return userType
    }

    fun setUserType(userType: String?) {
        this.userType = userType
    }

    fun getUserDevice(): String? {
        return userDevice
    }

    fun setUserDevice(userDevice: String?) {
        this.userDevice = userDevice
    }

    fun getAppVersion(): String? {
        return appVersion
    }

    fun setAppVersion(appVersion: String?) {
        this.appVersion = appVersion
    }

}