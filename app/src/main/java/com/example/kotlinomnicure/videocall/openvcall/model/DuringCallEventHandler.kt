package com.example.kotlinomnicure.videocall.openvcall.model

interface DuringCallEventHandler: AGEventHandler {
    fun onUserJoined(uid: Int)

    fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int)

    fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int)

    fun onUserOffline(uid: Int, reason: Int)
    fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int)
    fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int)

    fun onExtraCallback(type: Int, vararg data: Any?)
}