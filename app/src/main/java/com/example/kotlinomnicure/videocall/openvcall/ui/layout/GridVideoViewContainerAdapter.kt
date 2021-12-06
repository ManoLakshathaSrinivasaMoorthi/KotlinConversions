package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.videocall.propeller.UserStatusData
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import org.slf4j.LoggerFactory
import java.lang.NullPointerException
import java.lang.String
import java.util.ArrayList
import java.util.HashMap
import kotlin.math.ceil
import kotlin.math.sqrt

class GridVideoViewContainerAdapter//        log.debug("GridVideoViewContainerAdapter " + (mLocalUid & 0xFFFFFFFFL));
    (
    activity: Activity?,
    localUid: Int,
    localStatus: Int,
    localAudioStatus: Int,
    uids: HashMap<Int, SurfaceView>,
    providerList: ArrayList<Provider>
) : VideoViewAdapter() {
    private val log = LoggerFactory.getLogger(
        GridVideoViewContainerAdapter::class.java
    )

    override fun customizedInit(uids: HashMap<Int, SurfaceView>?, force: Boolean) {
        mUsers?.let {
            VideoViewAdapterUtil().composeDataItem1(it, uids, mLocalUid,
                mLocalStatus,
                mLocalAudioStatus
            )
        } // local uid
        setProfileInUsers()
        if (force || mItemWidth === 0 || mItemHeight === 0) {
            val windowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(outMetrics)
            val count = uids?.size
            var DividerX = 1
            var DividerY = 1
            when {
                count == 2 -> {
                    DividerY = 2
                }
                count == 3 -> { // FIX for 3 users = 1 + 2 peer
                    DividerX = getNearestSqrt(4)
                    DividerY = ceil((4 * 1f / DividerX).toDouble()).toInt()
                }
                count!! > 3 -> {
                    DividerX = getNearestSqrt(count)
                    DividerY = ceil((count * 1f / DividerX).toDouble()).toInt()
                }
            }
            val width = outMetrics.widthPixels
            val height = outMetrics.heightPixels
            if (width > height) {
                mItemWidth = width / DividerY
                mItemHeight = height / DividerX
            } else {
                mItemWidth = width / DividerX
                mItemHeight = height / DividerY
            }
        }
    }

    override fun notifyUiChanged(
        uids: HashMap<Int?, SurfaceView?>?,
        uidExtra: Int,
        status: HashMap<Int?, Int?>?,
        audioStatus: HashMap<Int?, Int?>?,
        volume: HashMap<Int?, Int?>?
    )  {
        var status = status
        var audioStatus = audioStatus
        if (status == null) {
            status = HashMap()
            if (mUsers != null) {
                for (userStatusData in mUsers!!) {
                    status[userStatusData.getmUid()] = userStatusData.getmStatus()
                }
            }
        }
        if (audioStatus == null) {
            audioStatus = HashMap()
            if (mUsers != null) {
                for (userStatusData in mUsers!!) {
                    audioStatus[userStatusData.getmUid()] = userStatusData.getmAudioStatus()
                }
            }
        }
        setLocalUid(uidExtra)
        mUsers?.let {
            uids?.let { it1 ->
                VideoViewAdapterUtil().composeDataItem(
                    it,
                    it1,
                    uidExtra,
                    status,
                    audioStatus,
                    volume,
                    mVideoInfo
                )
            }
        }
        setProfileInUsers()
        notifyDataSetChanged()
        if (DEBUG) {
            log.debug("notifyUiChanged " + (mLocalUid and 0xFFFFFFFFL.toInt()).toString() + " " + (uidExtra and 0xFFFFFFFFL.toInt()).toString() + " " + uids.toString() + " " + status.toString() + " " + volume)
        }
    }


    private fun getNearestSqrt(n: Int): Int {
        return sqrt(n.toDouble()).toInt()
    }



    override  fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return parent.let { super.onCreateViewHolder(it, viewType) }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder: VideoUserStatusHolder = holder as VideoUserStatusHolder
        val user: UserStatusData? = mUsers?.get(position)
        if (DEBUG) {
            log.debug("onBindViewHolder " + position + " " + user + " " + myHolder + " " + myHolder.itemView + " " + mDefaultChildItem)
        }
        val holderView = myHolder.itemView as FrameLayout
        if (holderView.childCount == mDefaultChildItem) {
            val target: SurfaceView? = user?.getmView()
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
        user?.let { VideoViewAdapterUtil().renderExtraData(mContext, it, myHolder, true) }
    }

    override fun getItemCount(): Int {
        return mUsers?.size!!
    }

    override fun getItem(position: Int): UserStatusData? {
        return mUsers?.get(position)
    }

    override fun getItemId(position: Int): Long {
        val user: UserStatusData? = mUsers?.get(position)
        val view: SurfaceView = user?.getmView()
            ?: throw NullPointerException(
                "SurfaceView destroyed for user " + (user?.getmUid()?.and(0xFFFFFFFFL.toInt())).toString() + " " + user?.getmStatus()
                    .toString() + " " + user?.getmVolume()
            )
        return (String.valueOf(user.getmUid()) + System.identityHashCode(view)).hashCode().toLong()
    }
}
