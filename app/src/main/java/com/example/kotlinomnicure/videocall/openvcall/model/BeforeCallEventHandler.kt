package com.example.kotlinomnicure.videocall.openvcall.model

import io.agora.rtc.IRtcEngineEventHandler.LastmileProbeResult

interface BeforeCallEventHandler :AGEventHandler {
    fun onLastmileQuality(quality: Int)

    fun onLastmileProbeResult(result: LastmileProbeResult?)
}
