package com.example.kotlinomnicure.videocall.openvcall.model

interface AGEventHandler {
    companion object {
        val EVENT_TYPE_ON_DATA_CHANNEL_MSG=3

        var EVENT_TYPE_ON_USER_VIDEO_MUTED = 6

        var EVENT_TYPE_ON_USER_AUDIO_MUTED = 7

        var EVENT_TYPE_ON_SPEAKER_STATS = 8

        var EVENT_TYPE_ON_AGORA_MEDIA_ERROR = 9

        var EVENT_TYPE_ON_USER_VIDEO_STATS = 10

        var EVENT_TYPE_ON_APP_ERROR = 13

        var EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED = 18
    }
}