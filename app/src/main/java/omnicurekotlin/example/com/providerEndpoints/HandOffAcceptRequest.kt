package omnicurekotlin.example.com.providerEndpoints

import java.io.Serializable



class HandOffAcceptRequest : Serializable {

    var providerId: Long? = null

    var patientId: Long? = null
    @JvmName("getProviderId1")
    fun getProviderId(): Long? {
        return providerId
    }

    @JvmName("setProviderId1")
    fun setProviderId(providerId: Long?) {
        this.providerId = providerId
    }

    @JvmName("getPatientId1")
    fun getPatientId(): Long? {
        return patientId
    }

    @JvmName("setPatientId1")
    fun setPatientId(patientId: Long?) {
        this.patientId = patientId
    }

}