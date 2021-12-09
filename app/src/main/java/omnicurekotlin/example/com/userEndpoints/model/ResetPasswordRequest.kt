package omnicurekotlin.example.com.userEndpoints.model

class ResetPasswordRequest{

     var email:String?=null
     var oldPassword:String?=null
     var passwordNew:String?=null
     var confirmPassword:String?=null
     var token:String?=null
     var countryCode:String?=null
     var phoneNumber:String?=null

    @JvmName("getEmail1")
    fun getEmail(): String? {
        return email
    }

    @JvmName("setEmail1")
    fun setEmail(email: String?) {
        this.email = email
    }

    @JvmName("getOldPassword1")
    fun getOldPassword(): String? {
        return oldPassword
    }

    @JvmName("setOldPassword1")
    fun setOldPassword(oldPassword: String?) {
        this.oldPassword = oldPassword
    }

    @JvmName("getPasswordNew1")
    fun getPasswordNew(): String? {
        return passwordNew
    }

    @JvmName("setPasswordNew1")
    fun setPasswordNew(passwordNew: String?) {
        this.passwordNew = passwordNew
    }

    @JvmName("getConfirmPassword1")
    fun getConfirmPassword(): String? {
        return confirmPassword
    }

    @JvmName("setConfirmPassword1")
    fun setConfirmPassword(confirmPassword: String?) {
        this.confirmPassword = confirmPassword
    }

    @JvmName("getToken1")
    fun getToken(): String? {
        return token
    }

    @JvmName("setToken1")
    fun setToken(token: String?) {
        this.token = token
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
}
