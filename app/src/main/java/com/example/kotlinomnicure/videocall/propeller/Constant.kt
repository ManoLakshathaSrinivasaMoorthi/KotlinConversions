package com.example.kotlinomnicure.videocall.propeller

import io.agora.rtc.video.BeautyOptions

class Constant {

    var MEDIA_SDK_VERSION: String? = null

    val MIX_FILE_PATH = "/assets/ringtone.mp3" // in assets folder


    val SHOW_VIDEO_INFO = false

    private var DEBUG_INFO_ENABLED = false // Show debug/log info on screen


    private var BEAUTY_EFFECT_ENABLED = false // Built-in face beautification


    private val BEAUTY_EFFECT_DEFAULT_CONTRAST = 1
    private val BEAUTY_EFFECT_DEFAULT_LIGHTNESS = .7f
    private val BEAUTY_EFFECT_DEFAULT_SMOOTHNESS = .5f
    private val BEAUTY_EFFECT_DEFAULT_REDNESS = .1f

    val BEAUTY_OPTIONS = BeautyOptions(
        BEAUTY_EFFECT_DEFAULT_CONTRAST,
        BEAUTY_EFFECT_DEFAULT_LIGHTNESS,
        BEAUTY_EFFECT_DEFAULT_SMOOTHNESS,
        BEAUTY_EFFECT_DEFAULT_REDNESS
    )

    val BEAUTY_EFFECT_MAX_LIGHTNESS = 1.0f
    val BEAUTY_EFFECT_MAX_SMOOTHNESS = 1.0f
    val BEAUTY_EFFECT_MAX_REDNESS = 1.0f

    fun isDebugInfoEnabled(): Boolean {
        return DEBUG_INFO_ENABLED
    }

    fun setDebugInfoEnabled(debugInfoEnabled: Boolean) {
        DEBUG_INFO_ENABLED = debugInfoEnabled
    }

    fun isBeautyEffectEnabled(): Boolean {
        return BEAUTY_EFFECT_ENABLED
    }

    fun setBeautyEffectEnabled(beautyEffectEnabled: Boolean) {
        BEAUTY_EFFECT_ENABLED = beautyEffectEnabled
    }
}
