package com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model

import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.providerEndpoints.model.SendChatMessageOutuputResponseModel
import java.io.Serializable


class SendChatMessageOutuputResponseModel : Serializable
     {

    var dischargedCount: Double? = null
        private set
    /**
     * @return value or `null` for none
     */
    //    public Integer getErrorId() {
    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    //    private Integer errorId;
    var errorId: Double? = null
        private set
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    var errorMessage: String? = null
        private set

    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    private var chatMessages: ChatMessages? = null

    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    var chatMessageStatusList: List<ChatMessageStatusModel>? = null
    /**
     * @return value or `null` for none
     */
    /**
     * The value may be `null`.
     */
    //@com.google.api.client.util.Key
    var status: Boolean? = null
        private set
    var chatId: String? = null

    /**
     * @param dischargedCount dischargedCount or `null` for none
     */
    //    public SendChatMessageOutuputResponseModel setDischargedCount(Integer dischargedCount) {
    fun setDischargedCount(dischargedCount: Double?): SendChatMessageOutuputResponseModel {
        this.dischargedCount = dischargedCount
        return this
    }

    /**
     * @param errorId errorId or `null` for none
     */
    fun setErrorId(errorId: Double?): SendChatMessageOutuputResponseModel {
//    public SendChatMessageOutuputResponseModel setErrorId(Integer errorId) {
        this.errorId = errorId
        return this
    }

    /**
     * @param errorMessage errorMessage or `null` for none
     */
    fun setErrorMessage(errorMessage: String?): SendChatMessageOutuputResponseModel {
        this.errorMessage = errorMessage
        return this
    }

    /**
     * @return value or `null` for none
     */
    fun getChatMessages(): ChatMessages? {
        return chatMessages
    }

    /**
     * @param chatMessages room or `null` for none
     */
    fun setChatMessages(chatMessages: ChatMessages?): SendChatMessageOutuputResponseModel {
        this.chatMessages = chatMessages
        return this
    }

    /**
     * @param status status or `null` for none
     */
    fun setStatus(status: Boolean?): SendChatMessageOutuputResponseModel {
        this.status = status
        return this
    }
}