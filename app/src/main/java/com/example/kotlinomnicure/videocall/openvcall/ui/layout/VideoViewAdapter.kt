package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.videocall.propeller.UserStatusData
import com.example.kotlinomnicure.videocall.propeller.VideoInfoData
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import org.slf4j.LoggerFactory
import java.lang.NullPointerException
import java.lang.String
import java.util.ArrayList
import java.util.HashMap

abstract class VideoViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val log = LoggerFactory.getLogger(VideoViewAdapter::class.java)

    val DEBUG = false

    private var mInflater: LayoutInflater? = null
    protected var mContext: Context? = null

    protected var mUsers: ArrayList<UserStatusData>? = null
    protected var mProviderList: ArrayList<Provider>? = null


    protected var mLocalUid = 0
    protected var mLocalStatus = 0
    protected var mLocalAudioStatus = 0

    fun VideoViewAdapter(
        activity: Activity,
        localUid: Int,
        localStatus: Int,
        localAudioStatus: Int,
        uids: HashMap<Int, SurfaceView>,
        providerList: ArrayList<Provider>?
    ) {
        mInflater = activity.layoutInflater
        mContext = activity.applicationContext
        mLocalUid = localUid
        mLocalStatus = localStatus
        mLocalAudioStatus = localAudioStatus
        mUsers = ArrayList<UserStatusData>()
        mProviderList = ArrayList<Provider>()
        if (providerList != null) {
            mProviderList!!.addAll(providerList)
        }
        init(uids)
    }

    protected var mItemWidth = 0
    protected var mItemHeight = 0

    protected var mDefaultChildItem = 0

    private fun init(uids: HashMap<Int, SurfaceView>) {
        mUsers!!.clear()
        customizedInit(uids, true)
    }

    abstract fun customizedInit(uids: HashMap<Int, SurfaceView>?, force: Boolean)

    abstract fun notifyUiChanged(
        uids: HashMap<Int?, SurfaceView?>?,
        uidExtra: Int,
        status: HashMap<Int?, Int?>?,
        audioStatus: HashMap<Int?, Int?>?,
        volume: HashMap<Int?, Int?>?
    )

    protected var mVideoInfo // left user should removed from this HashMap
            : HashMap<Int, VideoInfoData>? = null

    fun addVideoInfo(uid: Int, video: VideoInfoData) {
        if (mVideoInfo == null) {
            mVideoInfo = HashMap<Int, VideoInfoData>()
        }
        mVideoInfo!![uid] = video
    }

    fun cleanVideoInfo() {
        mVideoInfo = null
    }

    fun setLocalUid(uid: Int) {
        mLocalUid = uid
    }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater?.inflate(R.layout.video_view_container, parent, false) as ViewGroup
        v.layoutParams.width = mItemWidth
        v.layoutParams.height = mItemHeight
        mDefaultChildItem = v.childCount
        return VideoUserStatusHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as VideoUserStatusHolder
        val user: UserStatusData = mUsers!![position]
        if (DEBUG) {
//            log.debug("onBindViewHolder " + position + " " + user + " " + myHolder + " " + myHolder.itemView + " " + mDefaultChildItem);
        }
        val holderView = myHolder.itemView as FrameLayout
        if (holderView.childCount == mDefaultChildItem) {
            val target: SurfaceView? = user.getmView()
            target?.let { VideoViewAdapterUtil().stripView(it) }
            holderView.addView(
                target,
                0,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }
        VideoViewAdapterUtil().renderExtraData(mContext, user, myHolder)
    }

    override fun getItemCount(): Int {
        if (DEBUG) {
//            log.debug("getItemCount " + mUsers.size());
        }
        return mUsers!!.size
    }

    override fun getItemId(position: Int): Long {
        val user: UserStatusData = mUsers!![position]
        val view: SurfaceView = user.getmView()
            ?: throw NullPointerException(
                "SurfaceView destroyed for user " + user.getmUid()
                    .toString() + " " + user.getmStatus().toString() + " " + user.getmVolume()
            )
        return (String.valueOf(user.getmUid()) + System.identityHashCode(view)).hashCode().toLong()
    }

    fun getItemUid(position: Int): Int {
        val user: UserStatusData = mUsers!![position]
        return user.getmUid()
    }

    open fun getItem(position: Int): UserStatusData? {
        return mUsers!![position]
    }

    fun getUserList(): List<UserStatusData>? {
        return mUsers
    }


    protected fun setProfileInUsers() {
        for (i in mUsers!!.indices) {
            val provider: Provider? = getProviderFromList(mUsers!![i].getmUid())
            if (provider != null) {
                mUsers!![i].setmName(provider.getName())
                mUsers!![i].setmProfilePic(provider.getProfilePicUrl())
            }
        }
    }

    private fun getProviderFromList(uid: Int): Provider? {
        for (provider in mProviderList!!) {
            if (provider?.getId()?.equals(Any?) === uid) run run {
                return provider
            }
        }
        return null
    }

    fun getmProviderList(): ArrayList<Provider>? {
        return mProviderList
    }

    fun setmProviderList(mProviderList: ArrayList<Provider>?) {
        this.mProviderList = mProviderList
    }
}
