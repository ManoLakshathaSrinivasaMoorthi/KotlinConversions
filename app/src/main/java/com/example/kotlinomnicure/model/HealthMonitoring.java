package com.example.kotlinomnicure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HealthMonitoring {

    @Expose
    @SerializedName("notificationRequests")
    private List<NotificationRequests> notificationRequests;
    @Expose
    @SerializedName("dischargedCount")
    private int dischargedCount;
    @Expose
    @SerializedName("errorId")
    private int errorId;
    @Expose
    @SerializedName("status")
    private boolean status;
    @Expose
    @SerializedName("errorMessage")
    private String errorMessage;

    public boolean isStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Expose
    @SerializedName("title")
    private String title;

    public List<NotificationRequests> getNotificationRequests() {
        return notificationRequests;
    }

    public void setNotificationRequests(List<NotificationRequests> notificationRequests) {
        this.notificationRequests = notificationRequests;
    }

    public int getDischargedCount() {
        return dischargedCount;
    }

    public void setDischargedCount(int dischargedCount) {
        this.dischargedCount = dischargedCount;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static class NotificationRequests {
        @Expose
        @SerializedName("notificationEnabled")
        private boolean notificationEnabled;
        @Expose
        @SerializedName("acuity")
        private String acuity;

        public boolean getNotificationEnabled() {
            return notificationEnabled;
        }

        public void setNotificationEnabled(boolean notificationEnabled) {
            this.notificationEnabled = notificationEnabled;
        }

        public String getAcuity() {
            return acuity;
        }

        public void setAcuity(String acuity) {
            this.acuity = acuity;
        }

        @Override
        public String toString() {
            return "NotificationRequests{" +
                    "notificationEnabled=" + notificationEnabled +
                    ", acuity='" + acuity + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HealthMonitoring{" +
                "notificationRequests=" + notificationRequests +
                ", dischargedCount=" + dischargedCount +
                ", errorId=" + errorId +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
