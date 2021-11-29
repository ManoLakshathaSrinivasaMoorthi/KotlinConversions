package com.mvp.omnicure.kotlinactivity.requestbodys

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginDetailsRequestBody {
    // provided by backend
    @Expose
    @SerializedName("appVersion")
    private var appVersion: String? = null

    @Expose
    @SerializedName("fcmkey")
    private var fcmkey: String? = null

    @Expose
    @SerializedName("opSysType")
    private var opSysType: String? = null

    @Expose
    @SerializedName("email")
    private var email: String? = null

    //extra 3 keys added
    @Expose
    @SerializedName("password")
    private var password: String? = null


    //    Unrecognized field \"returnSecureToken\" backend error
    @Expose
    @SerializedName("returnSecureToken")
    private val returnSecureToken: String? = null

    @Expose
    @SerializedName("overrride")
    private var overrride: String? = null

    fun LoginDetailsRequestBody(
        appVersion: String?,
        fcmkey: String?,
        opSysType: String?,
        email: String?,
        password: String?,
        overrride: String?
    ) {
        this.appVersion = appVersion
        this.fcmkey = fcmkey
        this.opSysType = opSysType
        this.email = email
        this.password = password
        //        this.returnSecureToken = returnSecureToken;
        this.overrride = overrride
    }
}