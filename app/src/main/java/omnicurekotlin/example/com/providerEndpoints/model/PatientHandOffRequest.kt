package omnicurekotlin.example.com.providerEndpoints.model

class PatientHandOffRequest {
    //com.google.api.client.util.Key
    private var bspProviderId: String? = null

    //com.google.api.client.util.Key
    private var otherBspProviderId: String? = null

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
}
