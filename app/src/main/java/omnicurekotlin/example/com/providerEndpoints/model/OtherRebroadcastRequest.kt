package omnicurekotlin.example.com.providerEndpoints.model

class OtherRebroadcastRequest {

    private var bspProviderId: String? = null
    private var otherBspProviderId: String? = null
    private var patientId: String? = null

    fun getBspProviderId(): String? {
        return bspProviderId
    }

    fun setBspProviderId(bspProviderId: String?) {
        this.bspProviderId = bspProviderId
    }

    fun getOtherBspProviderId(): String? {
        return otherBspProviderId
    }

    fun setOtherBspProviderId(otherBspProviderId: String?) {
        this.otherBspProviderId = otherBspProviderId
    }

    fun getPatientId(): String? {
        return patientId
    }

    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }


}