package omnicurekotlin.example.com.patientsEndpoints.model

class PatientOtpModel {

    private var id: String? = null
    private var smsOtp: String? = null

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getSmsOtp(): String? {
        return smsOtp
    }

    fun setSmsOtp(smsOtp: String?) {
        this.smsOtp = smsOtp
    }
}