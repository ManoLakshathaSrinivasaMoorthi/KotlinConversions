package omnicurekotlin.example.com.patientsEndpoints.model

class PatientTransferRequest {


     var patientId: String? = null
     var wardName: String? = null
     var providerId: String? = null
     var hospitalId: String? = null
     var summaryNote: String? = null
     var token: String? = null

    @JvmName("getToken1")
    fun getToken(): String? {
        return token
    }

    @JvmName("setToken1")
    fun setToken(token: String?) {
        this.token = token
    }
    @JvmName("getPatientId1")
    fun getPatientId(): String? {
        return patientId
    }

    @JvmName("setPatientId1")
    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }

    @JvmName("getWardName1")
    fun getWardName(): String? {
        return wardName
    }

    @JvmName("setWardName1")
    fun setWardName(wardName: String?) {
        this.wardName = wardName
    }

    @JvmName("getProviderId1")
    fun getProviderId(): String? {
        return providerId
    }

    @JvmName("setProviderId1")
    fun setProviderId(providerId: String?) {
        this.providerId = providerId
    }

    @JvmName("getHospitalId1")
    fun getHospitalId(): String? {
        return hospitalId
    }

    @JvmName("setHospitalId1")
    fun setHospitalId(hospitalId: String?) {
        this.hospitalId = hospitalId
    }

    @JvmName("getSummaryNote1")
    fun getSummaryNote(): String? {
        return summaryNote
    }

    @JvmName("setSummaryNote1")
    fun setSummaryNote(summaryNote: String?) {
        this.summaryNote = summaryNote
    }
}
