package omnicurekotlin.example.com.providerEndpoints.model

import java.io.Serializable


class SendChatMessageOutuputResponseModel : Serializable
     {

    var dischargedCount: Double? = null
    var errorId: Double? = null
    var errorMessage: String? = null
    private var chatMessages: ChatMessages? = null
    var chatMessageStatusList: List<ChatMessageStatusModel>? = null
    var status: Boolean? = null
    var chatId: String? = null

    fun setDischargedCount(dischargedCount: Double?): SendChatMessageOutuputResponseModel {
        this.dischargedCount = dischargedCount
        return this
    }


    fun setErrorId(errorId: Double?): SendChatMessageOutuputResponseModel {
        this.errorId = errorId
        return this
    }


    fun setErrorMessage(errorMessage: String?): SendChatMessageOutuputResponseModel {
        this.errorMessage = errorMessage
        return this
    }


    fun getChatMessages(): ChatMessages? {
        return chatMessages
    }


    fun setChatMessages(chatMessages: ChatMessages?): SendChatMessageOutuputResponseModel {
        this.chatMessages = chatMessages
        return this
    }


    fun setStatus(status: Boolean?): SendChatMessageOutuputResponseModel {
        this.status = status
        return this
    }
}