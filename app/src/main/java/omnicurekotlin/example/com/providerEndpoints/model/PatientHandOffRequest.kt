package omnicurekotlin.example.com.providerEndpoints.model

class PatientHandOffRequest {

    var bspProviderId: String? = null
    var otherBspProviderId: String? = null

    @JvmName("getBspProviderId1")
    fun getBspProviderId(): String? {
        return bspProviderId
    }

    @JvmName("setBspProviderId1")
    fun setBspProviderId(bspProviderId: String?) {
        this.bspProviderId = bspProviderId
    }

    @JvmName("getOtherBspProviderId1")
    fun getOtherBspProviderId(): String? {
        return otherBspProviderId
    }

    @JvmName("setOtherBspProviderId1")
    fun setOtherBspProviderId(otherBspProviderId: String?) {
        this.otherBspProviderId = otherBspProviderId
    }
}
