package omnicurekotlin.example.com.providerEndpoints.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class HandOffListResponse {
    @Expose
    @SerializedName("currentProvider")
    private var currentProvider: CurrentProvider? = null

    @Expose
    @SerializedName("otherBspList")
    private var otherBspList: ArrayList<OtherBspList?>? = null

    @Expose
    @SerializedName("errorId")
    private var errorId = 0

    @Expose
    @SerializedName("status")
    private var status = false

    fun HandOffListResponse() {}

    fun getCurrentProvider(): CurrentProvider? {
        return currentProvider
    }

    fun setCurrentProvider(currentProvider: CurrentProvider?) {
        this.currentProvider = currentProvider
    }

    fun getOtherBspList(): ArrayList<OtherBspList?>? {
        return otherBspList
    }

    fun setOtherBspList(otherBspList: ArrayList<OtherBspList?>?) {
        this.otherBspList = otherBspList
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

    class OtherBspList {
        @JvmName("getName1")
        fun getName(): String? {
       return  name
        }

        @JvmName("getId1")
        fun getId(): String? {
   return id
        }

        @Expose
        @SerializedName("emailOtpVerified")
        var emailOtpVerified = false

        @Expose
        @SerializedName("smsOtpVerified")
        var smsOtpVerified = false

        @Expose
        @SerializedName("status")
        var status: String? = null

        @Expose
        @SerializedName("approveRejectRemarks")
        var approveRejectRemarks: String? = null

        @Expose
        @SerializedName("lcpType")
        var lcpType: String? = null

        @Expose
        @SerializedName("providerType")
        var providerType: String? = null

        @Expose
        @SerializedName("activeHour")
        var activeHour = 0

        @Expose
        @SerializedName("numberOfPatient")
        var numberOfPatient = 0

        @Expose
        @SerializedName("screenName")
        var screenName: String? = null

        @Expose
        @SerializedName("osType")
        var osType: String? = null

        @Expose
        @SerializedName("fcmKey")
        var fcmKey: String? = null

        @Expose
        @SerializedName("healthMonitoringTime")
        var healthMonitoringTime: String? = null

        @Expose
        @SerializedName("token")
        var token: String? = null

        @Expose
        @SerializedName("joiningTime")
        var joiningTime: String? = null

        @Expose
        @SerializedName("emailOtp")
        var emailOtp: String? = null

        @Expose
        @SerializedName("hospital")
        var hospital: String? = null

        @Expose
        @SerializedName("hospitalId")
        var hospitalId: String? = null

        @Expose
        @SerializedName("countryCode")
        var countryCode: String? = null

        @Expose
        @SerializedName("phone")
        var phone: String? = null

        @Expose
        @SerializedName("role")
        var role: String? = null

        @Expose
        @SerializedName("password")
        var password: String? = null

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

    class CurrentProvider {
        @JvmName("getId1")
        fun getId(): String? {
         return  id
        }

        @Expose
        @SerializedName("emailOtpVerified")
        var emailOtpVerified = false

        @Expose
        @SerializedName("smsOtpVerified")
        var smsOtpVerified = false

        @Expose
        @SerializedName("status")
        var status: String? = null

        @Expose
        @SerializedName("lcpType")
        var lcpType: String? = null

        @Expose
        @SerializedName("providerType")
        var providerType: String? = null

        @Expose
        @SerializedName("activeHour")
        var activeHour = 0

        @Expose
        @SerializedName("numberOfPatient")
        var numberOfPatient = 0

        @Expose
        @SerializedName("screenName")
        var screenName: String? = null

        @Expose
        @SerializedName("osType")
        var osType: String? = null

        @Expose
        @SerializedName("healthMonitoringTime")
        var healthMonitoringTime: String? = null

        @Expose
        @SerializedName("token")
        var token: String? = null

        @Expose
        @SerializedName("joiningTime")
        var joiningTime: String? = null

        @Expose
        @SerializedName("emailOtp")
        var emailOtp: String? = null

        @Expose
        @SerializedName("hospital")
        var hospital: String? = null

        @Expose
        @SerializedName("hospitalId")
        var hospitalId: String? = null

        @Expose
        @SerializedName("countryCode")
        var countryCode: String? = null

        @Expose
        @SerializedName("phone")
        var phone: String? = null

        @Expose
        @SerializedName("role")
        var role: String? = null

        @Expose
        @SerializedName("password")
        var password: String? = null

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
