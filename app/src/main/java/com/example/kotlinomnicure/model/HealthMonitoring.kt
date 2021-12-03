package com.example.kotlinomnicure.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HealthMonitoring {

    @Expose
    @SerializedName("notificationRequests")
    private var notificationRequests: List<NotificationRequests?>? = null

    @Expose
    @SerializedName("dischargedCount")
    private var dischargedCount = 0

    @Expose
    @SerializedName("errorId")
    private var errorId = 0

    @Expose
    @SerializedName("status")
    private var status = false

    @Expose
    @SerializedName("errorMessage")
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

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    @Expose
    @SerializedName("title")
    private var title: String? = null

    fun getNotificationRequests(): List<NotificationRequests?>? {
        return notificationRequests
    }

    fun setNotificationRequests(notificationRequests: List<NotificationRequests?>?) {
        this.notificationRequests = notificationRequests
    }

    fun getDischargedCount(): Int {
        return dischargedCount
    }

    fun setDischargedCount(dischargedCount: Int) {
        this.dischargedCount = dischargedCount
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

    class NotificationRequests {
        @Expose
        @SerializedName("notificationEnabled")
        var notificationEnabled = false

        @Expose
        @SerializedName("acuity")
        var acuity: String? = null

        override fun toString(): String {
            return "NotificationRequests{" +
                    "notificationEnabled=" + notificationEnabled +
                    ", acuity='" + acuity + '\'' +
                    '}'
        }
    }

    override fun toString(): String {
        return "HealthMonitoring{" +
                "notificationRequests=" + notificationRequests +
                ", dischargedCount=" + dischargedCount +
                ", errorId=" + errorId +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", title='" + title + '\'' +
                '}'
    }
}
