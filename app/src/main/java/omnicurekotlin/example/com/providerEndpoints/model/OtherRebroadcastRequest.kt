package omnicurekotlin.example.com.providerEndpoints.model

class OtherRebroadcastRequest {

     var bspProviderId: String? = null
     var otherBspProviderId: String? = null
     var patientId: String? = null

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

    @JvmName("getPatientId1")
    fun getPatientId(): String? {
        return patientId
    }

    @JvmName("setPatientId1")
    fun setPatientId(patientId: String?) {
        this.patientId = patientId
    }


}