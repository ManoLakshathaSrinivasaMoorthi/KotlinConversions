package com.example.kotlinomnicure.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginModel {

    constructor() {}

    @Expose
    @SerializedName("agoraAppCertificate")
    private var agoraAppCertificate: String? = null

    @Expose
    @SerializedName("agoraAppId")
    private var agoraAppId: String? = null

    @Expose
    @SerializedName("aesEncryptionKey")
    private var aesEncryptionKey: String? = null

    @Expose
    @SerializedName("encryptionKey")
    private var encryptionKey: String? = null

    @Expose
    @SerializedName("refreshToken")
    private var refreshToken: String? = null

    @Expose
    @SerializedName("idToken")
    private var idToken: String? = null

    @Expose
    @SerializedName("tutorial_url")
    private var tutorial_url: String? = null

    @Expose
    @SerializedName("feedbackForm")
    private var feedbackForm: String? = null

    @Expose
    @SerializedName("dischargedCount")
    private var dischargedCount = 0

    @Expose
    @SerializedName("provider")
    private var provider: Provider? = null

    @Expose
    @SerializedName("statusMessage")
    private var statusMessage: String? = null

    @Expose
    @SerializedName("errorId")
    private var errorId = 0

    @Expose
    @SerializedName("status")
    private var status = false

    @Expose
    @SerializedName("errorMessage;")
    private var errorMessage: String? = null

    fun isStatus(): Boolean {
        return status
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getAgoraAppCertificate(): String? {
        return agoraAppCertificate
    }

    fun setAgoraAppCertificate(agoraAppCertificate: String?) {
        this.agoraAppCertificate = agoraAppCertificate
    }

    fun getAgoraAppId(): String? {
        return agoraAppId
    }

    fun setAgoraAppId(agoraAppId: String?) {
        this.agoraAppId = agoraAppId
    }

    fun getAesEncryptionKey(): String? {
        return aesEncryptionKey
    }

    fun setAesEncryptionKey(aesEncryptionKey: String?) {
        this.aesEncryptionKey = aesEncryptionKey
    }

    fun getEncryptionKey(): String? {
        return encryptionKey
    }

    fun setEncryptionKey(encryptionKey: String?) {
        this.encryptionKey = encryptionKey
    }

    fun getRefreshToken(): String? {
        return refreshToken
    }

    fun setRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }

    fun getIdToken(): String? {
        return idToken
    }

    fun setIdToken(idToken: String?) {
        this.idToken = idToken
    }

    fun getTutorial_url(): String? {
        return tutorial_url
    }

    fun setTutorial_url(tutorial_url: String?) {
        this.tutorial_url = tutorial_url
    }

    fun getFeedbackForm(): String? {
        return feedbackForm
    }

    fun setFeedbackForm(feedbackForm: String?) {
        this.feedbackForm = feedbackForm
    }

    fun getDischargedCount(): Int {
        return dischargedCount
    }

    fun setDischargedCount(dischargedCount: Int) {
        this.dischargedCount = dischargedCount
    }

    fun getProvider(): Provider? {
        return provider
    }

    fun setProvider(provider: Provider?) {
        this.provider = provider
    }

    fun getStatusMessage(): String? {
        return statusMessage
    }

    fun setStatusMessage(statusMessage: String?) {
        this.statusMessage = statusMessage
    }

    fun getErrorId(): Int {
        return errorId
    }

    fun setErrorId(errorId: Int) {
        this.errorId = errorId
    }

    fun getStatus(): Boolean {
        return status
    }

    fun setStatus(status: Boolean) {
        this.status = status
    }

    class Provider {
        @Expose
        @SerializedName(value = "status", alternate = ["Status"])
        var status: String? = null

        @Expose
        @SerializedName("userId")
        var userId: String? = null

        @Expose
        @SerializedName("remoteProviderType")
        var remoteProviderType: String? = null

        @Expose
        @SerializedName("token")
        var token: String? = null

        @Expose
        @SerializedName("hospital")
        var hospital: String? = null

        @Expose
        @SerializedName("phone")
        var phone: String? = null

        @Expose
        @SerializedName("role")
        var role: String? = null

        @Expose
        @SerializedName("email")
        var email: String? = null

        @Expose
        @SerializedName("lname")
        var lname: String? = null

        @Expose
        @SerializedName("fname")
        var fname: String? = null

        @Expose
        @SerializedName("name")
        var name: String? = null

        @Expose
        @SerializedName("id")
        var id: String? = null
    }
}
