package omnicurekotlin.example.com.healthcareEndPoints.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public final class ProviderNotificationResponse implements Serializable {


//    @com.google.api.client.util.Key
//    private Boolean status;
//    @com.google.api.client.util.Key
//    private Integer errorId;
//    @com.google.api.client.util.Key
//    private String errorMessage;
//    @com.google.api.client.util.Key
//    private ProviderNotificationInputResponse notificationSettings;
//    @com.google.api.client.util.Key
//    private List<NotificationSettingsRequest> notificationRequests = null;
//
//    public String getErrorMessage() {
//        return errorMessage;
//    }
//
//    public void setErrorMessage(String errorMessage) {
//        this.errorMessage = errorMessage;
//    }
//
//    public Boolean getStatus() {
//        return status;
//    }
//
//    public void setStatus(Boolean status) {
//        this.status = status;
//    }
//
//    public Integer getErrorId() {
//        return errorId;
//    }
//
//    public void setErrorId(Integer errorId) {
//        this.errorId = errorId;
//    }
//
//    public ProviderNotificationInputResponse getProviderNotification() {
//        return notificationSettings;
//    }
//
//    public void setProviderNotification(ProviderNotificationInputResponse providerNotification) {
//        this.notificationSettings = providerNotification;
//    }
//
//    public List<NotificationSettingsRequest> getNotificationRequests() {
//        return notificationRequests;
//    }
//
//    public void setNotificationRequests(List<NotificationSettingsRequest> notificationRequests) {
//        this.notificationRequests = notificationRequests;
//    }

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("errorId")
    @Expose
    private Integer errorId;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("notificationSettings")
    @Expose
    private ProviderNotificationInputResponse notificationSettings;
    @SerializedName("notificationRequests")
    @Expose
    private List<NotificationSettingsRequest> notificationRequests = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ProviderNotificationInputResponse getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(ProviderNotificationInputResponse notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public List<NotificationSettingsRequest> getNotificationRequests() {
        return notificationRequests;
    }

    public void setNotificationRequests(List<NotificationSettingsRequest> notificationRequests) {
        this.notificationRequests = notificationRequests;
    }


}
