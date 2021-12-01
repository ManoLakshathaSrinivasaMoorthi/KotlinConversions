package omnicurekotlin.example.com.providerEndpoints.model

class HandOffAcceptRequest {

    private var patientId: String? = null
    private var providerId: String? = null

    fun getPatientId(): String? {
        return patientId
    }

    fun setPatientId(patientId: Long?): Unit {
        this.patientId = patientId
    }

    fun getProviderId(): String? {
        return providerId
    }

    fun setProviderId(providerId: Long): Unit {
        this.providerId = providerId
    }
}
