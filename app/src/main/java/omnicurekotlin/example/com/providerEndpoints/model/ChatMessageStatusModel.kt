package omnicurekotlin.example.com.providerEndpoints.model

import java.io.Serializable


class ChatMessageStatusModel : Serializable {

    var receiverId: String? = null
    var providerName: String? = null
    var providerType: String? = null
    var profilePicUrl: String? = null
    var status: String? = null
    var time: Long = 0
}