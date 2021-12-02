package com.example.kotlinomnicure.videocall.propeller

class VideoInfoData {
    private var mWidth = 0
    private var mHeight = 0
    private var mDelay = 0
    private var mFrameRate = 0
    private var mBitRate = 0
    private var mCodec = 0

    fun getmWidth(): Int {
        return mWidth
    }

    fun setmWidth(mWidth: Int) {
        this.mWidth = mWidth
    }

    fun getmHeight(): Int {
        return mHeight
    }

    fun setmHeight(mHeight: Int) {
        this.mHeight = mHeight
    }

    fun getmDelay(): Int {
        return mDelay
    }

    fun setmDelay(mDelay: Int) {
        this.mDelay = mDelay
    }

    fun getmFrameRate(): Int {
        return mFrameRate
    }

    fun setmFrameRate(mFrameRate: Int) {
        this.mFrameRate = mFrameRate
    }

    fun getmBitRate(): Int {
        return mBitRate
    }

    fun setmBitRate(mBitRate: Int) {
        this.mBitRate = mBitRate
    }

    fun getmCodec(): Int {
        return mCodec
    }

    fun setmCodec(mCodec: Int) {
        this.mCodec = mCodec
    }

    fun VideoInfoData(
        width: Int,
        height: Int,
        delay: Int,
        frameRate: Int,
        bitRate: Int,
        codec: Int
    ) {
        mWidth = width
        mHeight = height
        mDelay = delay
        mFrameRate = frameRate
        mBitRate = bitRate
        mCodec = codec
    }

    constructor(width: Int, height: Int, delay: Int, frameRate: Int, bitRate: Int) :
            super()


    override fun toString(): String {
        return "VideoInfoData{" +
                "mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                ", mDelay=" + mDelay +
                ", mFrameRate=" + mFrameRate +
                ", mBitRate=" + mBitRate +
                ", mCodec=" + mCodec +
                '}'
    }
}