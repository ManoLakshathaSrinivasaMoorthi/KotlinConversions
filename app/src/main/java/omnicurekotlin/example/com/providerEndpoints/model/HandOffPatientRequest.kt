package omnicurekotlin.example.com.providerEndpoints.model

class HandOffPatientRequest {

    var patientId: String? = null
    var providerId: String? = null

    @JvmName("getPatientId1")
    fun getPatientId(): String? {
        return patientId
    }

    @JvmName("setPatientId1")
    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }

    @JvmName("getProviderId1")
    fun getProviderId(): String? {
        return providerId
    }

    @JvmName("setProviderId1")
    fun setProviderId(providerId: String?) {
        this.providerId = providerId
    }
}
