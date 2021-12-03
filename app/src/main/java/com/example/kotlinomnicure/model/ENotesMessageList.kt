package com.example.kotlinomnicure.model

import java.io.Serializable

class ENotesMessageList: Serializable {

    private var message: String? = null
    private var messageTime: String? = null

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getMessageTime(): String? {
        return messageTime
    }

    fun setMessageTime(messageTime: String?) {
        this.messageTime = messageTime
    }
}
