package omnicurekotlin.example.com.providerEndpoints

import java.io.Serializable



class HandOffAcceptRequest : Serializable {
    //    @com.google.api.client.util.Key
    var providerId: Long? = null

    //    @com.google.api.client.util.Key
    var patientId: Long? = null
}