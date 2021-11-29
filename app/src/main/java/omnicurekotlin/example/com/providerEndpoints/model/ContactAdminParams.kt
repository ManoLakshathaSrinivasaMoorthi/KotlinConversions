package omnicurekotlin.example.com.providerEndpoints.model

class ContactAdminParams {
    private var message: String? = null

    //com.google.api.client.util.Key
    private var providerId: Long? = null

    //com.google.api.client.util.Key
    private var userType: String? = null

    //com.google.api.client.util.Key
    private var userDevice: String? = null

    //com.google.api.client.util.Key
    private var appVersion: String? = null

    //com.google.api.client.util.Key
    private var hospitalName: String? = null

    //com.google.api.client.util.Key
    private var subUserType: String? = null

    //com.google.api.client.util.Key
    private var email: String? = null

    //com.google.api.client.util.Key
    private var userName: String? = null


    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

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
