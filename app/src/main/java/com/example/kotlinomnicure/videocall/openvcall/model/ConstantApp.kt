package com.example.kotlinomnicure.videocall.openvcall.model

import io.agora.rtc.Constants
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtc.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions

class ConstantApp {
    val APP_BUILD_DATE = "today"

    val BASE_VALUE_PERMISSION = 0X0001
    val PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1
    val PERMISSION_REQ_ID_CAMERA = BASE_VALUE_PERMISSION + 2
    val PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3

    val MAX_PEER_COUNT = 4

    // please check details string_array_resolutions/string_array_frame_rate/string_array_bit_rate at strings_config.xml
    private val VIDEO_DIMENSIONS = arrayOf(
        VideoEncoderConfiguration.VD_160x120,
        VideoEncoderConfiguration.VD_320x180,
        VideoEncoderConfiguration.VD_320x240,
        VideoEncoderConfiguration.VD_640x360,
        VideoEncoderConfiguration.VD_640x480,
        VideoEncoderConfiguration.VD_1280x720
    )
    private val VIDEO_FPS = arrayOf(
        FRAME_RATE.FRAME_RATE_FPS_1,
        FRAME_RATE.FRAME_RATE_FPS_7,
        FRAME_RATE.FRAME_RATE_FPS_10,
        FRAME_RATE.FRAME_RATE_FPS_15,
        FRAME_RATE.FRAME_RATE_FPS_24,
        FRAME_RATE.FRAME_RATE_FPS_30
    )

    val DEFAULT_VIDEO_ENC_RESOLUTION_IDX = 4 // default use 720P

    val DEFAULT_VIDEO_ENC_FPS_IDX = 4 // default use 15fps


    fun getVideoDimensions(): Array<VideoDimensions>? {
        return VIDEO_DIMENSIONS
    }

    fun getVideoFps(): Array<FRAME_RATE>? {
        return VIDEO_FPS
    }

    object PrefManager {
        const val PREF_PROPERTY_VIDEO_ENC_RESOLUTION = "pref_profile_index"
        const val PREF_PROPERTY_VIDEO_ENC_FPS = "pref_ENC_fps"
        const val PREF_PROPERTY_UID = "pOCXx_uid"
    }

    val ACTION_KEY_CHANNEL_NAME = "ecHANEL"
    val ACTION_KEY_ENCRYPTION_KEY = "xdL_encr_key_"
    val ACTION_KEY_ENCRYPTION_MODE = "tOK_edsx_Mode"

    object AppError {
        const val NO_CONNECTION_ERROR = 3
    }

    fun getNetworkQualityDescription(quality: Int): String? {
        val inString: String
        inString = when (quality) {
            Constants.QUALITY_EXCELLENT -> "Excellent($quality)"
            Constants.QUALITY_GOOD -> "Good($quality)"
            Constants.QUALITY_POOR -> "Poor($quality)"
            Constants.QUALITY_BAD -> "Bad($quality)"
            Constants.QUALITY_VBAD -> "Very Bad($quality)"
            else -> "Unknown($quality)"
        }
        return inString
    }
}
