package omnicurekotlin.example.com.patientsEndpoints.model

class PatientTransferRequest {


    private var patientId: String? = null
    private var wardName: String? = null
    private var providerId: String? = null
    private var hospitalId: String? = null
    private var summaryNote: String? = null

    fun getPatientId(): String? {
        return patientId
    }

    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }

    fun getWardName(): String? {
        return wardName
    }

    fun setWardName(wardName: String?) {
        this.wardName = wardName
    }

    fun getProviderId(): String? {
        return providerId
    }

    fun setProviderId(providerId: String?) {
        this.providerId = providerId
    }

    fun getHospitalId(): String? {
        return hospitalId
    }

    fun setHospitalId(hospitalId: String?) {
        this.hospitalId = hospitalId
    }

    fun getSummaryNote(): String? {
        return summaryNote
    }

    fun setSummaryNote(summaryNote: String?) {
        this.summaryNote = summaryNote
    }
}
