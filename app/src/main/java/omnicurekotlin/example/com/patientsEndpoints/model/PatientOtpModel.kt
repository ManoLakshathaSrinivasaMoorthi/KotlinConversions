package omnicurekotlin.example.com.patientsEndpoints.model

class PatientOtpModel {

     var id: String? = null
     var smsOtp: String? = null

    @JvmName("getId1")
    fun getId(): String? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: String?) {
        this.id = id
    }

    @JvmName("getSmsOtp1")
    fun getSmsOtp(): String? {
        return smsOtp
    }

    @JvmName("setSmsOtp1")
    fun setSmsOtp(smsOtp: String?) {
        this.smsOtp = smsOtp
    }
}