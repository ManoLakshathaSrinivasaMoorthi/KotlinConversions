package com.example.kotlinomnicure.videocall.openvcall.model

import android.content.Context
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.IRtcEngineEventHandler.*
import io.agora.rtc.RtcEngine
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class MyEngineEventHandler : IRtcEngineEventHandler() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val mEventHandlerList = ConcurrentHashMap<AGEventHandler, Int>()

    fun addEventHandler(handler: AGEventHandler) {
        mEventHandlerList[handler] = 0
    }

    fun removeEventHandler(handler: AGEventHandler) {
        mEventHandlerList.remove(handler)
    }

    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
//        log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed)
            }
        }
    }

    override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
        log.debug("onFirstLocalVideoFrame $width $height $elapsed")
    }


    override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onRemoteVideoStateChanged(uid, state, reason, elapsed)
            }
        }
        log.debug(
            "onRemoteVideoStateChanged " +
                    "uid:" + uid +
                    "state:" + state +
                    "reason:" + reason +
                    "elapsed:" + elapsed
        )
    }

    override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onRemoteAudioStateChanged(uid, state, reason, elapsed)
            }
        }
        log.debug(
            ("onRemoteAudioStateChanged " +
                    "uid:" + uid +
                    "state:" + state +
                    "reason:" + reason +
                    "elapsed:" + elapsed)
        )
    }


    override fun onUserJoined(uid: Int, elapsed: Int) {
        log.debug("onUserJoined " + (uid and 0xFFFFFFFFL.toInt()) + elapsed)
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onUserJoined(uid)
            }
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        log.debug("onUserOffline " + (uid and 0xFFFFFFFFL.toInt()) + " " + reason)

        // FIXME this callback may return times
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onUserOffline(uid, reason)
            }
        }
    }

    override fun onUserMuteVideo(uid: Int, muted: Boolean) {
        log.debug("onUserMuteVideo " + (uid and 0xFFFFFFFFL.toInt()) + " " + muted)
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_MUTED, uid, muted)
            }
        }
    }

    override fun onRtcStats(stats: RtcStats?) {}


    override fun onRemoteVideoStats(stats: RemoteVideoStats) {
        log.debug("onRemoteVideoStats " + stats.uid + " " + stats.delay + " " + stats.receivedBitrate + " " + stats.rendererOutputFrameRate + " " + stats.width + " " + stats.height)
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(AGEventHandler().EVENT_TYPE_ON_USER_VIDEO_STATS, stats)
            }
        }
    }

    override fun onAudioVolumeIndication(speakerInfos: Array<AudioVolumeInfo?>?, totalVolume: Int) {
        if (speakerInfos == null) {
            // quick and dirty fix for crash
            // TODO should reset UI for no sound
            return
        }
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(
                    AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS,
                    speakerInfos as Any?
                )
            }
        }
    }

    override fun onLeaveChannel(stats: RtcStats?) {
        //Log.d("onLeaveChannel", stats.toString());
    }

    override fun onLastmileQuality(quality: Int) {
        log.debug("onLastmileQuality $quality")
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is BeforeCallEventHandler) {
                handler.onLastmileQuality(quality)
            }
        }
    }

    override fun onLastmileProbeResult(result: LastmileProbeResult) {
        log.debug("onLastmileProbeResult $result")
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is BeforeCallEventHandler) {
                handler.onLastmileProbeResult(result)
            }
        }
    }

    override fun onError(error: Int) {
        log.debug("onError-Agora " + error + " " + RtcEngine.getErrorDescription(error))
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(
                    AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR,
                    error,
                    RtcEngine.getErrorDescription(error)
                )
            }
        }
    }

    override fun onTokenPrivilegeWillExpire(token: String?) {
        super.onTokenPrivilegeWillExpire(token)
//        Log.e("Agora call","onTokenPrivilegeWillExpire "+token);
    }

    override fun onStreamMessage(uid: Int, streamId: Int, data: ByteArray?) {
//        log.debug("onStreamMessage " + (uid & 0xFFFFFFFFL) + " " + streamId + " " + Arrays.toString(data));
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_DATA_CHANNEL_MSG, uid, data)
            }
        }
    }

    override fun onStreamMessageError(
        uid: Int,
        streamId: Int,
        error: Int,
        missed: Int,
        cached: Int
    ) {
        log.warn("onStreamMessageError " + (uid and 0xFFFFFFFFL) + " " + streamId + " " + error + " " + missed + " " + cached)
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(
                    AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR,
                    error,
                    "on stream msg error " + (uid and 0xFFFFFFFFL) + " " + missed + " " + cached
                )
            }
        }
    }

    override fun onConnectionLost() {
        log.debug("onConnectionLost")
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(
                    AGEventHandler.EVENT_TYPE_ON_APP_ERROR,
                    ConstantApp.AppError.NO_CONNECTION_ERROR
                )
            }
        }
    }

    var mRtcEngine: RtcEngine? = null
    var _context: Context? = null
    fun setEngine(ctx: Context?, engine: RtcEngine?) {
        mRtcEngine = engine
        _context = ctx
    }

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        log.debug("onJoinChannelSuccess " + channel + " " + (uid and 0xFFFFFFFFL.toInt()) + "(" + uid + ") " + elapsed)
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onJoinChannelSuccess(channel, uid, elapsed)
            }
        }
    }

    override fun onAudioRouteChanged(routing: Int) {
        log.debug("onAudioRouteChanged $routing")
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED, routing)
            }
        }
    }

    override fun onWarning(warn: Int) {
        log.debug("onWarning $warn")
        val msg = "Check io.agora.rtc.Constants for details"
        val it: Iterator<AGEventHandler> = mEventHandlerList.keys.iterator()
        while (it.hasNext()) {
            val handler = it.next()
            if (handler is DuringCallEventHandler) {
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR, warn, msg)
            }
        }
    }

    override fun onAudioMixingStateChanged(state: Int, errorCode: Int) {
        log.debug("onAudioMixingStateChanged() state = [$state], errorCode = [$errorCode]")
    }
}
