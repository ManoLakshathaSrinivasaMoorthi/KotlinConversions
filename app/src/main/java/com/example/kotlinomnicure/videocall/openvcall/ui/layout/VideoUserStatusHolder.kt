package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.R
import com.example.kotlinomnicure.customview.CircularImageView

class VideoUserStatusHolder: RecyclerView.ViewHolder {
    var mMaskView: RelativeLayout? = null
    var mMaskViewContainer: RelativeLayout? = null
    var mAvatar: ImageView? = null
    var mCircularAvtar: CircularImageView? = null
    var mAvtarName: TextView? = null
    var mAvtarImageText: TextView? = null
    var mIndicator: ImageView? = null

    var mVideoInfo: LinearLayout? = null

    var mMetaData: TextView? = null

    constructor(v: View): super(v) {

        mMaskView = v.findViewById<View>(R.id.user_control_mask) as RelativeLayout
        mMaskViewContainer = v.findViewById<View>(R.id.user_overlay) as RelativeLayout
        mAvatar = v.findViewById<View>(R.id.default_avatar) as ImageView
        mCircularAvtar = v.findViewById<View>(R.id.default_avatar_circular) as CircularImageView
        mAvtarName = v.findViewById<View>(R.id.default_avatar_name) as TextView
        mIndicator = v.findViewById<View>(R.id.indicator) as ImageView
        mVideoInfo = v.findViewById<View>(R.id.video_info_container) as LinearLayout
        mMetaData = v.findViewById<View>(R.id.video_info_metadata) as TextView
        mAvtarImageText = v.findViewById<View>(R.id.avtar_image_text) as TextView
    }
}
