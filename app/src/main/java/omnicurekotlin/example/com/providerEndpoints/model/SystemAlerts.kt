package omnicurekotlin.example.com.providerEndpoints.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SystemAlerts {
    @Expose
    @SerializedName("systemAlertList")
    var systemAlertList: List<SystemAlertList?>? = null

    @Expose
    @SerializedName("status")
    var status = false

    @Expose
    @SerializedName("errorId")
    private var errorId = 0

    @Expose
    @SerializedName("errorMessage")
    var errorMessage: String? = null

    fun isStatus(): Boolean {
        return status
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getSystemAlertList(): List<SystemAlertList?>? {
        return systemAlertList
    }

    fun setSystemAlertList(systemAlertList: List<SystemAlertList?>?) {
        this.systemAlertList = systemAlertList
    }

    fun getStatus(): Boolean {
        return status
    }

    fun setStatus(status: Boolean) {
        this.status = status
    }

    fun getErrorId(): Int {
        return errorId
    }

    fun setErrorId(errorId: Int) {
        this.errorId = errorId
    }

    class SystemAlertList {
        @Expose
        @SerializedName("createdTime")
        var createdTime: String? = null

        @Expose
        @SerializedName("alertMsg")
        var alertMsg: String? = null

        @Expose
        @SerializedName("title")
        var title: String? = null

        @Expose
        @SerializedName("id")
        var id: String? = null
    }
}
