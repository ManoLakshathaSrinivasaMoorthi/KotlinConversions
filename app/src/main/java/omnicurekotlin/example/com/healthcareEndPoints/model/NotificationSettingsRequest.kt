package omnicurekotlin.example.com.healthcareEndPoints.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable

class NotificationSettingsRequest : Serializable {
    @SerializedName("acuity")
    @Expose
    var acuity: String? = null


    @SerializedName("notificationEnabled")
    @Expose
    var notificationEnabled: Boolean? = null

}