package com.example.kotlinomnicure.videocall.openvcall.model

class CurrentUserSettings {
    private var mEncryptionModeIndex = 0

    private var mEncryptionKey: String? = null

    private var mChannelName: String? = null

    fun getmEncryptionModeIndex(): Int {
        return mEncryptionModeIndex
    }

    constructor(mEncryptionModeIndex: Int) {
        this.mEncryptionModeIndex = mEncryptionModeIndex
    }

    fun getmEncryptionKey(): String? {
        return mEncryptionKey
    }


    fun setmEncryptionKey(mEncryptionKey: String) {
        this.mEncryptionKey = mEncryptionKey
    }


    fun getmChannelName(): String? {
        return mChannelName
    }

    fun setmChannelName(mChannelName: String) {
        this.mChannelName = mChannelName
    }

    fun CurrentUserSettings() {
        reset()
    }

    fun reset() {}
}
