package omnicurekotlin.example.com.providerEndpoints.model

class PatientHandOffRequest {
    var bspProviderId: String? = null
    var otherBspProviderId: String? = null

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
