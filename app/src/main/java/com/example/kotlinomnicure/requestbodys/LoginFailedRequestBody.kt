package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginFailedRequestBody {
    @Expose
    @SerializedName("osType")
    private var osType: String? = null

    @Expose
    @SerializedName("token")
    private var token: String? = null

    @Expose
    @SerializedName("loginStatus")
    private var loginStatus: String? = null

    @Expose
    @SerializedName("isOverrride")
    private var isOverrride: String? = null

    @Expose
    @SerializedName("otp")
    private var otp: String? = null

    @Expose
    @SerializedName("password")
    private var password: String? = null

    @Expose
    @SerializedName("countryCode")
    private var countryCode: String? = null

    @Expose
    @SerializedName("phone")
    private var phone: String? = null

    @Expose
    @SerializedName("email")
    private var email: String? = null

    fun getOsType(): String? {
        return osType
    }

    fun setOsType(osType: String?) {
        this.osType = osType
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getLoginStatus(): String? {
        return loginStatus
    }

    fun setLoginStatus(loginStatus: String?) {
        this.loginStatus = loginStatus
    }

    fun getIsOverrride(): String? {
        return isOverrride
    }

    fun setIsOverrride(isOverrride: String?) {
        this.isOverrride = isOverrride
    }

    fun getOtp(): String? {
        return otp
    }

    fun setOtp(otp: String?) {
        this.otp = otp
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getPhone(): String? {
        return phone
    }

    fun setPhone(phone: String?) {
        this.phone = phone
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }
}