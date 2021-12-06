package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.videocall.propeller.UserStatusData
import com.example.kotlinomnicure.videocall.propeller.VideoInfoData
import com.example.kotlinomnicure.videocall.propeller.ui.RecyclerItemClickListener
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import org.slf4j.LoggerFactory
import java.util.ArrayList
import java.util.HashMap
import kotlin.math.sqrt

class GridVideoViewContainer: RecyclerView {
    private val log = LoggerFactory.getLogger(GridVideoViewContainer::class.java)

    private var mGridVideoViewContainerAdapter: GridVideoViewContainerAdapter? = null

    constructor(context: Context?) :super(context!!)

   constructor(context: Context?, attrs: AttributeSet?) :super(context!!, attrs)

   constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) :super(context!!, attrs, defStyle)


    fun setItemEventHandler(listener: RecyclerItemClickListener.OnItemClickListener?) {
        this.addOnItemTouchListener(RecyclerItemClickListener(context, listener))
    }

    private fun initAdapter(activity: Activity, localUid: Int, localStatus: Int, localAudioStatus: Int,
        uids: HashMap<Int, SurfaceView>,
        providerList: ArrayList<Provider>): Boolean {
        if (mGridVideoViewContainerAdapter == null) {
            mGridVideoViewContainerAdapter = GridVideoViewContainerAdapter(activity, localUid, localStatus, localAudioStatus, uids, providerList)
            mGridVideoViewContainerAdapter?.setHasStableIds(true)
            return true
        }
        return false
    }

    fun initViewContainer(
        activity: Activity,
        localUid: Int,
        localStatus: Int,
        localAudioStatus: Int,
        uids: HashMap<Int, SurfaceView>,
        isLandscape: Boolean,
        providerList: ArrayList<Provider>
    ) {
        val newCreated =
            initAdapter(activity, localUid, localStatus, localAudioStatus, uids, providerList)
        if (!newCreated) {
            mGridVideoViewContainerAdapter?.setLocalUid(localUid)
            mGridVideoViewContainerAdapter?.customizedInit(uids, true)
        }
        this.adapter = mGridVideoViewContainerAdapter
        val orientation = if (isLandscape) HORIZONTAL else VERTICAL
        val count = uids.size
        when {
            count <= 2 -> { // only local full view or or with one peer
                this.layoutManager = LinearLayoutManager(
                    activity.applicationContext,
                    orientation,
                    false
                )
            }
            count == 3 -> {
                val itemSpanCount = 2 // FIX for 3 users = 1 + 2 peer
                this.layoutManager = GridLayoutManager(
                    activity.applicationContext,
                    itemSpanCount,
                    orientation,
                    false
                )
            }
            count > 3 -> {
                val itemSpanCount = getNearestSqrt(count)
                //Log.d("VIDEO","itemSpanCount="+ itemSpanCount + "; count=" + count);
                this.layoutManager = GridLayoutManager(
                    activity.applicationContext,
                    itemSpanCount,
                    orientation,
                    false
                )
            }
        }
        mGridVideoViewContainerAdapter?.notifyDataSetChanged()
    }

    private fun getNearestSqrt(n: Int): Int {
        return sqrt(n.toDouble()).toInt()
    }

    fun notifyUiChanged(
        uids: HashMap<Int?, SurfaceView?>?,
        localUid: Int,
        status: HashMap<Int?, Int?>?,
        audioStatus: HashMap<Int?, Int?>?,
        volume: HashMap<Int?, Int?>?
    ) {
        if (mGridVideoViewContainerAdapter == null) {
            return
        }
        mGridVideoViewContainerAdapter!!.notifyUiChanged(uids, localUid, status, audioStatus, volume)
    }

    fun addVideoInfo(uid: Int, video: VideoInfoData?) {
        if (mGridVideoViewContainerAdapter == null) {
            return
        }
        video?.let { mGridVideoViewContainerAdapter!!.addVideoInfo(uid, it) }
    }

    fun cleanVideoInfo() {
        if (mGridVideoViewContainerAdapter == null) {
            return
        }
        mGridVideoViewContainerAdapter?.cleanVideoInfo()
    }

    fun getItem(position: Int): UserStatusData? {
        return mGridVideoViewContainerAdapter?.getItem(position)
    }

}
