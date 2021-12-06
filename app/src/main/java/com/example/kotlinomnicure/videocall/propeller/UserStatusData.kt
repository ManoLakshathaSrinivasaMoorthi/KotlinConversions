package com.example.kotlinomnicure.videocall.propeller

import android.view.SurfaceView

class UserStatusData {
    val DEFAULT_STATUS = 0
    val VIDEO_MUTED = 1
    val AUDIO_MUTED = VIDEO_MUTED shl 1

    private val DEFAULT_VOLUME = 0
    private var mUid = 0
    private var mView: SurfaceView? = null
    private var mStatus // if status is null, do nothing
            : Int? = null
    private var mAudioStatus: Int? = null
    private var mVolume = 0
    private var mName: String? = null
    private var mProfilePic: String? = null
    private var mVideoInfo: VideoInfoData? = null


    constructor(
        uid: Int,
        view: SurfaceView?,
        status: Int?,
        audioStatus: Int?,
        volume: Int,
        name: String?,
        profilePic: String?,

    ) :
        super()


    constructor(uid: Int, view: SurfaceView?, status: Int?, audioStatus: Int?, volume: Int):
            super()


   constructor(
        uid: Int,
        view: SurfaceView?,
        status: Int?,
        audioStatus: Int?,
        volume: Int,
        i: VideoInfoData?
    ):super()

    constructor()


    fun UserStatusData(
        uid: Int,
        view: SurfaceView?,
        status: Int?,
        audioStatus: Int?,
        volume: Int,
        i: VideoInfoData?,
        name: String?,
        profilePic: String?
    ) {
        mUid = uid
        mView = view
        mStatus = status
        mVolume = volume
        mVideoInfo = i
        mName = name
        mProfilePic = profilePic
        mAudioStatus = audioStatus
    }

    fun setVideoInfo(video: VideoInfoData?) {
        mVideoInfo = video
    }

    fun getVideoInfoData(): VideoInfoData? {
        return mVideoInfo
    }

    override fun toString(): String {
        return "UserStatusData{" +
                "mUid=" + (mUid and 0XFFFFFFFFL.toInt()) +
                ", mView=" + mView +
                ", mStatus=" + mStatus +
                ", mVolume=" + mVolume +
                '}'
    }


    fun getDefaultVolume(): Int {
        return DEFAULT_VOLUME
    }

    fun getmUid(): Int {
        return mUid
    }

    fun setmUid(mUid: Int) {
        this.mUid = mUid
    }

    fun getmView(): SurfaceView? {
        return mView
    }

    fun setmView(mView: SurfaceView?) {
        this.mView = mView
    }

    fun getmStatus(): Int? {
        return mStatus
    }

    fun setmStatus(mStatus: Int?) {
        this.mStatus = mStatus
    }

    fun getmAudioStatus(): Int? {
        return mAudioStatus
    }

    fun setmAudioStatus(mAudioStatus: Int?) {
        this.mAudioStatus = mAudioStatus
    }

    fun getmVolume(): Int {
        return mVolume
    }

    fun setmVolume(mVolume: Int) {
        this.mVolume = mVolume
    }

    fun getmName(): String? {
        return mName
    }

    fun setmName(mName: String?) {
        this.mName = mName
    }

    fun getmProfilePic(): String? {
        return mProfilePic
    }

    fun setmProfilePic(mProfilePic: String?) {
        this.mProfilePic = mProfilePic
    }

    fun getmVideoInfo(): VideoInfoData? {
        return mVideoInfo
    }

    fun setmVideoInfo(mVideoInfo: VideoInfoData?) {
        this.mVideoInfo = mVideoInfo
    }
}
