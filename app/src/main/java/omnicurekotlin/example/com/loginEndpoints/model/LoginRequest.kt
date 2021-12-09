
package omnicurekotlin.example.com.loginEndpoints.model

import java.io.Serializable


class LoginRequest : Serializable {

    var countryCode: String? = null
    var email: String? = null
    var phoneNumber: String? = null
    var otp: String? = null
    var password: String? = null
    var token: String? = null
    var phone: String? = null
    var overrride: Boolean? = null


    var loginStatus: Boolean? = null


    fun setCountryCode(countryCode: String?): LoginRequest {
        this.countryCode = countryCode
        return this
    }


    fun setEmail(email: String?): LoginRequest {
        this.email = email
        return this
    }


    fun setOtp(otp: String?): LoginRequest {
        this.otp = otp
        return this
    }


    fun setPassword(password: String?): LoginRequest {
        this.password = password
        return this
    }


    fun setPhone(phone: String?): LoginRequest {
        this.phone = phone
        return this
    }
    @JvmName("getToken1")
    fun getToken(): String? {
        return token
    }

    @JvmName("setToken1")
    fun setToken(token: String?) {
        this.token = token
    }

}