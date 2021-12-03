package com.example.kotlinomnicure.model

import java.io.Serializable

class ENotesList: Serializable {
    private var date: String? = null
    private var messages: List<ENotesMessageList?>? = null

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    fun getMessages(): List<ENotesMessageList?>? {
        return messages
    }

    fun setMessages(messages: List<ENotesMessageList?>?) {
        this.messages = messages
    }

}
