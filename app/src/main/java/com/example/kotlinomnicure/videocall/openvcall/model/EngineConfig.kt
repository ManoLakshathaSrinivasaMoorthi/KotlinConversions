package com.example.kotlinomnicure.videocall.openvcall.model

class EngineConfig {
     var mUid = 0

     var mChannel: String? = null

    fun getmUid(): Int {
        return mUid
    }

    fun setmUid(mUid: Int) {
        this.mUid = mUid
    }

    fun getmChannel(): String? {
        return mChannel
    }

    fun setmChannel(mChannel: String?) {
        this.mChannel = mChannel
    }

    fun reset() {
        mChannel = null
    }

    fun EngineConfig() {}
}