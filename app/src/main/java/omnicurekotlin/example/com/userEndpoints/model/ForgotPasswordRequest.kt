package omnicurekotlin.example.com.userEndpoints.model

class ForgotPasswordRequest
{
    var email:String?=null
     var countryCode:String?=null
     var phoneNumber:String?=null
     var token:String?=null
     var otp:String?=null

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    @JvmName("setEmail1")
    fun setEmail(email: String?) {
        this.email = email
    }

    @JvmName("getCountryCode1")
    fun getCountryCode(): String? {
        return countryCode
    }

    @JvmName("setCountryCode1")
    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    @JvmName("getPhoneNumber1")
    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    @JvmName("setPhoneNumber1")
    fun setPhoneNumber(phoneNumber: String?) {
        this.phoneNumber = phoneNumber
    }

    @JvmName("getToken1")
    fun getToken(): String? {
        return token
    }

    @JvmName("setToken1")
    fun setToken(token: String?) {
        this.token = token
    }

    @JvmName("getOtp1")
    fun getOtp(): String? {
        return otp
    }

    @JvmName("setOtp1")
    fun setOtp(otp: String?) {
        this.otp = otp
    }
}