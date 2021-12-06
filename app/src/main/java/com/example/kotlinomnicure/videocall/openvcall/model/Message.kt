package com.example.kotlinomnicure.videocall.openvcall.model

class Message(sender: User?, content: String?) {
    private var mSender: User? = null

    private var mContent: String? = null

    private var mType = 0

    fun Message(type: Int, sender: User?, content: String?) {
        mType = type
        mSender = sender
        mContent = content
    }

    fun getSender(): User? {
        return mSender
    }

    fun getContent(): String? {
        return mContent
    }

    fun getType(): Int {
        return mType
    }

    val MSG_TYPE_TEXT = 1 // CHANNEL

}