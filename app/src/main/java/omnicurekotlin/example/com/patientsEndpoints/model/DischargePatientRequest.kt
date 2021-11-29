package omnicurekotlin.example.com.patientsEndpoints.model

class DischargePatientRequest {


    private var patientId: String? = null
    private var dischargeSummary: String? = null

    fun getPatientId(): String? {
        return patientId
    }

    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }

    fun getDischargeSummary(): String? {
        return dischargeSummary
    }

    fun setDischargeSummary(dischargeSummary: String?) {
        this.dischargeSummary = dischargeSummary
    }

}