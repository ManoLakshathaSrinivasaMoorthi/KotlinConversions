package omnicurekotlin.example.com.patientsEndpoints.model

class DischargePatientRequest {


     var patientId: String? = null
     var dischargeSummary: String? = null

    @JvmName("getPatientId1")
    fun getPatientId(): String? {
        return patientId
    }

    @JvmName("setPatientId1")
    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }

    @JvmName("getDischargeSummary1")
    fun getDischargeSummary(): String? {
        return dischargeSummary
    }

    @JvmName("setDischargeSummary1")
    fun setDischargeSummary(dischargeSummary: String?) {
        this.dischargeSummary = dischargeSummary
    }

}