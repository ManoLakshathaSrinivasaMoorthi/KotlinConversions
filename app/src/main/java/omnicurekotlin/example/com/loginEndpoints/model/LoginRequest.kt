package omnicurekotlin.example.com.loginEndpoints.model

class LoginRequest {


    private var countryCode: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null
    private var otp: String? = null
    private var password: String? = null
    private var token: String? = null
    private var phone: String? = null
    private var overrride: Boolean? = null
    private var loginStatus: Boolean? = null


    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    fun setPhoneNumber(phoneNumber: String?) {
        this.phoneNumber = phoneNumber
    }

    fun getLoginStatus(): Boolean? {
        return loginStatus
    }

    fun setLoginStatus(loginStatus: Boolean?) {
        this.loginStatus = loginStatus
    }

    fun getCountryCode(): String? {
        return countryCode
    }


    fun setCountryCode(countryCode: String?): LoginRequest? {
        this.countryCode = countryCode
        return this
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getOverrride(): Boolean? {
        return overrride
    }

    fun setOverrride(overrride: Boolean?) {
        this.overrride = overrride
    }


    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?): LoginRequest? {
        this.email = email
        return this
    }

    fun getOtp(): String? {
        return otp
    }


    fun setOtp(otp: String?): LoginRequest? {
        this.otp = otp
        return this
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?): LoginRequest? {
        this.password = password
        return this
    }

    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String?): LoginRequest? {
        this.phone = phone
        return this
    }

//  @Override
//  public LoginRequest set(String fieldName, Object value) {
//    return (LoginRequest) super.set(fieldName, value);
//  }
//
//  @Override
//  public LoginRequest clone() {
//    return (LoginRequest) super.clone();
//  }

}
