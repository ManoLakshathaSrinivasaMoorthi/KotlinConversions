package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import omnicurekotlin.example.com.providerEndpoints.model.Provider
import org.slf4j.LoggerFactory
import java.util.ArrayList
import java.util.HashMap

  class SmallVideoViewAdapter : VideoViewAdapter {

      private val log = LoggerFactory.getLogger(SmallVideoViewAdapter::class.java)

      private var mExceptedUid = 0

      constructor(
          activity: Activity?,
          localUid: Int,
          localStatus: Int,
          localAudioStatus: Int,
          exceptedUid: Int,
          uids: HashMap<Int?, SurfaceView?>?,
          providerList: ArrayList<Provider?>?
      ) :super(){

          mExceptedUid = exceptedUid
//        log.debug("SmallVideoViewAdapter " + (mLocalUid & 0xFFFFFFFFL) + " " + (mExceptedUid & 0xFFFFFFFFL));
      }

      override  fun customizedInit(uids: HashMap<Int?, SurfaceView?>?, force: Boolean) {
          val status = HashMap<Int, Int>()
          status[mLocalUid] = mLocalStatus
          val audioStatus = HashMap<Int, Int>()
          audioStatus[mLocalUid] = mLocalAudioStatus
          VideoViewAdapterUtil().composeDataItem(
              mUsers,
              uids,
              mLocalUid,
              status,
              audioStatus,
              null,
              mVideoInfo,
              mExceptedUid
          )
          setProfileInUsers()
          if (force || mItemWidth === 0 || mItemHeight === 0) {
              val windowManager =
                  mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
              val outMetrics = DisplayMetrics()
              windowManager.defaultDisplay.getMetrics(outMetrics)
              mItemWidth = outMetrics.widthPixels / 4
              mItemHeight = mItemWidth //outMetrics.heightPixels / 4;
          }
      }

      override  fun notifyUiChanged(
          uids: HashMap<Int, SurfaceView?>,
          uidExcepted: Int,
          status: HashMap<Int?, Int?>?,
          audioStatus: HashMap<Int?, Int?>?,
          volume: HashMap<Int?, Int?>?
      ) {
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
          //        mUsers.clear();
          mExceptedUid = uidExcepted
          log.debug("notifyUiChanged " + (mLocalUid and 0xFFFFFFFFL.toInt()).toString() + " " + (uidExcepted and 0xFFFFFFFFL).toString() + " " + uids.toString() + " " + status.toString() + " " + volume)
          mUsers?.let {
              VideoViewAdapterUtil().composeDataItem(
                  it,
                  uids,
                  mLocalUid,
                  status,
                  audioStatus,
                  volume,
                  mVideoInfo,
                  uidExcepted
              )
          }
          setProfileInUsers()
          notifyDataSetChanged()
      }

      open fun getExceptedUid(): Int {
          return mExceptedUid
      }


      override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
          val myHolder = holder as VideoUserStatusHolder
          val user: UserStatusData = mUsers!![position]
          Log.d(
              "TAG",
              "onBindViewHolder user: " + user.getmName()
                  .toString() + " status: " + user.getmStatus()
          )
          if (DEBUG) {
              log.debug("onBindViewHolder " + position + " " + user + " " + myHolder + " " + myHolder.itemView + " " + mDefaultChildItem)
          }
          val holderView = myHolder.itemView as FrameLayout
          if (holderView.childCount == mDefaultChildItem) {
              Log.d("TAG", "stip view called")
              val target: SurfaceView = user.getmView()
              VideoViewAdapterUtil().stripView(target)
              holderView.addView(
                  target,
                  0,
                  FrameLayout.LayoutParams(
                      ViewGroup.LayoutParams.MATCH_PARENT,
                      ViewGroup.LayoutParams.MATCH_PARENT
                  )
              )
          }
          VideoViewAdapterUtil().renderExtraData(mContext, user, myHolder, mExceptedUid, false)
      }

  }
