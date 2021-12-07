package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.utils.UtilityMethods
import com.example.kotlinomnicure.videocall.propeller.Constant
import com.example.kotlinomnicure.videocall.propeller.UserStatusData
import com.example.kotlinomnicure.videocall.propeller.VideoInfoData
import com.example.kotlinomnicure.videocall.propeller.ui.ViewUtil
import okhttp3.internal.and
import org.slf4j.LoggerFactory
import java.util.ArrayList
import java.util.HashMap

class VideoViewAdapterUtil {


    private val log = LoggerFactory.getLogger(VideoViewAdapterUtil::class.java)

    private val DEBUG = false

    fun composeDataItem1(users: ArrayList<UserStatusData>,
                         uids: HashMap<Int, SurfaceView?>, localUid: Int,
        localStatus: Int,
        localAudioStatus: Int
    ) {
//        Log.d("TAG", "composeDataItem1: "+localUid+" status: "+localStatus);
        for (entry in uids.entries) {
            if (DEBUG) {
//                log.debug("composeDataItem1 " + (entry.getKey() & 0xFFFFFFFFL) + " " + (localUid & 0xFFFFFFFFL) + " " + users.size() + " " + entry.getValue());
            }
            val surfaceV = entry.value
            surfaceV!!.setZOrderOnTop(false)
            surfaceV.setZOrderMediaOverlay(false)
            //            searchUidsAndAppend(users, entry, localUid, localStatus,localAudioStatus, UserStatusData.DEFAULT_VOLUME, null);
            searchUidsAndAppend(
                users,
                entry,
                localUid,
                localStatus,
                localAudioStatus,
                UserStatusData().getDefaultVolume(),
                null
            )
        }
        removeNotExisted(users, uids, localUid)
    }

    private fun removeNotExisted(
        users: ArrayList<UserStatusData>,
        uids: HashMap<Int, SurfaceView?>,
        localUid: Int
    ) {
        if (DEBUG) {
            log.debug("removeNotExisted all " + uids + " " + users.size)
        }
        val it = users.iterator()
        while (it.hasNext()) {
            val user = it.next()
            if (DEBUG) {
                log.debug("removeNotExisted $user $localUid")
            }
            if (uids[user.getmUid()] == null && user.getmUid() !== localUid) {
                it.remove()
            }
        }
    }

    private fun searchUidsAndAppend(
        users: ArrayList<UserStatusData>, entry: Map.Entry<Int, SurfaceView?>,
        localUid: Int, status: Int?, audioStatus: Int?, volume: Int, i: VideoInfoData?
    ) {
        if (entry.key == 0 || entry.key == localUid) {
            var found = false
            for (user in users) {
                if (user.getmUid() === entry.key && user.getmUid() === 0 || user.getmUid() === localUid) { // first time
                    user.setmUid(localUid)
                    if (status != null) {
//                        user.getmStatus() = status;
                        user.setmStatus(status)
                    }
                    if (audioStatus != null) {
//                        user.getmAudioStatus() = audioStatus;
                        user.setmAudioStatus(audioStatus)
                    }
                    //                    user.getmVolume() = volume;
                    user.setmVolume(volume)
                    user.setVideoInfo(i)
                    found = true
                    break
                }
            }
            if (!found) {
//                Log.d("TAG", "searchUidsAndAppend: "+localUid+" status: "+status);
                users.add(0, UserStatusData(localUid, entry.value, status, audioStatus, volume, i))
            }
        } else {
            var found = false
            for (user in users) {
                if (user.getmUid() === entry.key) {
                    if (status != null) {
                        user.setmStatus(status)
                    }
                    if (audioStatus != null) {
                        user.setmAudioStatus(audioStatus)
                    }
                    user.setmVolume(volume)
                    user.setVideoInfo(i)
                    found = true
                    break
                }
            }
            if (!found) {
                users.add(UserStatusData(entry.key, entry.value, status, audioStatus, volume, i))
            }
        }
    }

    fun composeDataItem(
        users: ArrayList<UserStatusData>, uids: HashMap<Int, SurfaceView?>,
        localUid: Int,
        status: HashMap<Int?, Int?>?,
        audioStatus: HashMap<Int?, Int?>?,
        volume: HashMap<Int?, Int?>?,
        video: HashMap<Int?, VideoInfoData?>?
    ) {
        composeDataItem(users, uids, localUid, status, audioStatus, volume, video, 0)
//        Log.d("TAG", "composeDataItem: "+localUid+" status: "+status.get(localUid));
    }

    fun composeDataItems(
        users: ArrayList<UserStatusData>, uids: HashMap<Int?, SurfaceView?>?,
        localUid: Int,
        status: HashMap<Int?, Int?>?,
        audioStatusMap: HashMap<Int?, Int?>?,
        volume: HashMap<Int?, Int?>?,
        video: HashMap<Int?, VideoInfoData?>?, uidExcepted: Int
    ) {
//        Log.d("TAG", "composeDataItem: "+localUid+" status: "+status.get(localUid)+ " uidExcepted: "+uidExcepted+" audiostatus: "+audioStatusMap.get(localUid));
        for (entry in uids.entries) {
            val uid = entry.key

//            if (uid == uidExcepted && uidExcepted != 0) {
//                continue;
//            }
            val local = uid == 0 || uid == localUid
            var s: Int? = null
            if (status != null) {
                s = status[uid]
                if (local && s == null) { // check again
                    s = status[if (uid == 0) localUid else 0]
                }
            }
            var audioStatus: Int? = null
            if (audioStatusMap != null) {
                audioStatus = audioStatusMap[uid]
                if (audioStatus == null) { // check again
                    audioStatus = UserStatusData().DEFAULT_STATUS
                }
            }
            var v: Int? = null
            if (volume != null) {
                v = volume[uid]
                if (local && v == null) { // check again
                    v = volume[if (uid == 0) localUid else 0]
                }
            }
            if (v == null) {
//                v = UserStatusData.DEFAULT_VOLUME;
                v = UserStatusData().getDefaultVolume()
            }
            var i: VideoInfoData?
            if (video != null) {
                i = video[uid]
                if (local && i == null) { // check again
                    i = video[if (uid == 0) localUid else 0]
                }
            } else {
                i = null
            }
            if (DEBUG) {
                log.debug("composeDataItem " + users + " " + entry + " " + (localUid and 0XFFFFFFFFL) + " " + s + " " + v + " " + i + " " + local + " " + (uid and 0XFFFFFFFFL) + " " + (uidExcepted and 0XFFFFFFFFL))
            }
            searchUidsAndAppend(users, entry, localUid, s, audioStatus, v!!, i)
        }
        removeNotExisted(users, uids, localUid)
    }

    fun renderExtraData(context: Context?, user: UserStatusData, myHolder: VideoUserStatusHolder) {
        renderExtraData(context, user, myHolder, -1, false)
    }

    fun renderExtraData(
        context: Context?,
        user: UserStatusData,
        myHolder: VideoUserStatusHolder,
        fromBigGrid: Boolean
    ) {
        renderExtraData(context, user, myHolder, -1, fromBigGrid)
    }

    fun renderExtraData(
        context: Context?,
        user: UserStatusData,
        myHolder: VideoUserStatusHolder,
        uidExcepted: Int,
        fromBigGrid: Boolean
    ) {
        if (DEBUG) {
            log.debug("renderExtraData $user $myHolder")
        }
        //        Log.d("TAG", "renderExtraData name" + user.mName + " status" + user.getmStatus() );
        if (user.getmStatus() != null) {
            if (user.getmStatus()!! and UserStatusData().VIDEO_MUTED !== 0) {

//                if(!TextUtils.isEmpty(user.mProfilePic)) {
                if (!TextUtils.isEmpty(user.getmProfilePic())) {
                    myHolder.mAvtarImageText!!.visibility = View.GONE
                    if (fromBigGrid) {
                        myHolder.mAvatar!!.visibility = View.GONE
                        myHolder.mCircularAvtar!!.visibility = View.VISIBLE
                        //                        Glide.with(context).load(user.mProfilePic).into(myHolder.mCircularAvtar);
                        Glide.with(context!!).load(user.getmProfilePic()).into(
                            myHolder.mCircularAvtar!!
                        )
                    } else {
                        myHolder.mAvatar!!.visibility = View.VISIBLE
                        myHolder.mCircularAvtar!!.visibility = View.GONE
                        //                        Glide.with(context).load(user.mProfilePic).into(myHolder.mAvatar);
                        Glide.with(context!!).load(user.getmProfilePic()).into(
                            myHolder.mAvatar!!
                        )
                    }
                } else {
                    myHolder.mAvtarImageText!!.visibility = View.VISIBLE
                    myHolder.mAvatar!!.visibility = View.GONE
                    myHolder.mCircularAvtar!!.visibility = View.GONE
                    myHolder.mAvtarImageText?.setText(user.getmName()?.let {
                        UtilityMethods().getNameText(
                            it
                        )
                    })
                    if (fromBigGrid) {
                        myHolder.mAvtarImageText!!.setBackgroundResource(R.drawable.text_image_drawable_white)
                    } else {
                        myHolder.mAvtarImageText!!.setBackgroundColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.gray_500
                            )
                        )
                    }
                }
                myHolder.mMaskView!!.setBackgroundResource(R.drawable.login_bg_gradient)
            } else {
                myHolder.mAvatar!!.visibility = View.GONE
                myHolder.mCircularAvtar!!.visibility = View.GONE
                myHolder.mAvtarImageText!!.visibility = View.GONE
                myHolder.mMaskView!!.setBackgroundColor(Color.TRANSPARENT)
            }
            myHolder.mAvtarName!!.text = getTrimmedText(user.getmName(), 12)
            if (user.getmAudioStatus() != null && user.getmAudioStatus() !== 0) {
//                Log.i("TAG","audiosStatus: "+user.getmAudioStatus()+" :::::::UserStatusData.AUDIO_MUTED: "+UserStatusData.AUDIO_MUTED);
                myHolder.mIndicator!!.setImageResource(R.drawable.icon_muted)
                myHolder.mIndicator!!.visibility = View.VISIBLE
                myHolder.mIndicator!!.tag = System.currentTimeMillis()
                //Log.d("Volume","is muted");
                return
            } else {
//                Log.i("TAG","audiosStatus: "+user.getmAudioStatus()+" :::::::UserStatusData.AUDIO_MUTED: "+UserStatusData.AUDIO_MUTED);
                myHolder.mIndicator!!.tag = null
                myHolder.mIndicator!!.visibility = View.INVISIBLE
            }
        }
        //set border frame
        if (uidExcepted == user.getmUid() && uidExcepted != -1) {
//            Log.d("TAG", "renderExtraData border name" + user.mName + " uidExcepted " + uidExcepted+" UID: " +user.getmUid());
            myHolder.mMaskViewContainer!!.setBackgroundResource(R.drawable.boarder_bg_white)
        } else {
//            Log.d("TAG", "renderExtraData no border name" + user.mName + " uidExcepted " + uidExcepted+" UID: " +user.getmUid());
            myHolder.mMaskViewContainer!!.setBackgroundColor(Color.TRANSPARENT)
        }
        if (fromBigGrid) {
            myHolder.mAvtarName!!.visibility = View.GONE
        } else {
            myHolder.mAvtarName!!.visibility = View.VISIBLE
        }
        //Log.d("Volume","user.getmStatus()=" + user.getmStatus());
        //Log.d("Volume","UserStatusData.AUDIO_MUTED" + UserStatusData.AUDIO_MUTED);
        val tag = myHolder.mIndicator!!.tag
        if (tag != null && System.currentTimeMillis() - tag as Long < 1500) { // workaround for audio volume comes just later than mute
            return
        }
        val volume = user.getmVolume()
        //Log.d("Volume","volume=" + volume + "; uid=" + user.getmUid());
        //Log.d("user data:","user data:" + user.toString());

//        if (volume > 0) {
//            myHolder.mIndicator.setImageResource(R.drawable.icon_speaker);
//            myHolder.mIndicator.setVisibility(View.VISIBLE);
//        } else {
//            myHolder.mIndicator.setVisibility(View.INVISIBLE);
//        }
        if (Constant().SHOW_VIDEO_INFO && user.getVideoInfoData() != null) {
            val videoInfo = user.getVideoInfoData()
            myHolder.mMetaData?.setText(context?.let { videoInfo?.let { it1 ->
                ViewUtil().composeVideoInfoString(it,
                    it1
                )
            } })
            //            if(videoInfo.mWidth==0 && videoInfo.mHeight==0) {
//                Log.d("Volume", "Video Muted by " + "uid=" + user.getmUid());
//                myHolder.mAvatar.setVisibility(View.VISIBLE);
//                myHolder.mMaskView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
//            } else {
//                myHolder.mAvatar.setVisibility(View.GONE);
//                myHolder.mMaskView.setBackgroundColor(Color.TRANSPARENT);
//            }
            myHolder.mVideoInfo!!.visibility = View.VISIBLE
        } else {
            myHolder.mVideoInfo!!.visibility = View.GONE
            //Log.d("Volume","Video gone=" + "; uid=" + user.getmUid());
        }
    }

    fun getTrimmedText(str: String?, length: Int): String? {
        if (TextUtils.isEmpty(str)) {
            return ""
        }
        return if (str!!.length < length + 3) {
            str
        } else {
            str.substring(0, length) + "..."
        }
    }

    fun stripView(view: SurfaceView) {
        val parent = view.parent
        if (parent != null) {
            (parent as FrameLayout).removeView(view)
        }
    }
}
