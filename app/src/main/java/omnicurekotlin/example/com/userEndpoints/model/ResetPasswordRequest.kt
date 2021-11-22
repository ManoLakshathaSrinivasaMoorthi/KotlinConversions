package omnicurekotlin.example.com.userEndpoints.model

class ResetPasswordRequest{
    private var email:String?=null
    private var oldPassword:String?=null
    private var passwordNew:String?=null
    private var confirmPassword:String?=null
    private var token:String?=null
    private var countryCode:String?=null
    private var phoneNumber:String?=null
    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getOldPassword(): String? {
        return oldPassword
    }

    fun setOldPassword(oldPassword: String?) {
        this.oldPassword = oldPassword
    }

    fun getPasswordNew(): String? {
        return passwordNew
    }

    fun setPasswordNew(passwordNew: String?) {
        this.passwordNew = passwordNew
    }

    fun getConfirmPassword(): String? {
        return confirmPassword
    }

    fun setConfirmPassword(confirmPassword: String?) {
        this.confirmPassword = confirmPassword
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    fun setPhoneNumber(phoneNumber: String?) {
        this.phoneNumber = phoneNumber
    }
}
