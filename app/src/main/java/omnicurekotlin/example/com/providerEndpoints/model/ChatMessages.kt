package omnicurekotlin.example.com.providerEndpoints.model

import java.io.Serializable

class ChatMessages : Serializable {

    var id: String? = null
    var senderId: String? = null
    var message: String? = null
    var type: String? = null
    var subType: String? = null
    var patientId: String? = null
    var consultId: String? = null
    var senderName: String? = null
    var time: String? = null
    var urgent: Boolean? = null
    var important: Boolean? = null
    var receiver: String? = null
    var senderProfilePic: String? = null
}