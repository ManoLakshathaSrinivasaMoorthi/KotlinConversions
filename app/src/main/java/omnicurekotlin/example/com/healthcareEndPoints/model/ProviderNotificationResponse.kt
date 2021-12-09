package omnicurekotlin.example.com.healthcareEndPoints.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProviderNotificationResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("errorId")
    @Expose
    var errorId: Int? = null

    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String? = null

    @SerializedName("notificationSettings")
    @Expose
    var notificationSettings: ProviderNotificationInputResponse? = null

    @SerializedName("notificationRequests")
    @Expose
    var notificationRequests: List<NotificationSettingsRequest>? = null
}