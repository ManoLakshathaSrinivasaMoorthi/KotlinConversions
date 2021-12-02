package com.example.kotlinomnicure.videocall.openvcall.ui.layout

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.videocall.openvcall.model.ConstantApp

class VideoEncResolutionAdapter: RecyclerView.Adapter<*> {

    private var mContext: Context? = null

    private var mSelectedIdx = 0

    constructor(context: Context?, selected: Int) {
        mContext = context
        mSelectedIdx = selected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_enc_resolution_item, parent, false)
        return VideoEncResolutionViewHolder(v)
    }



    override fun getItemCount(): Int {
        return mContext!!.resources.getStringArray(R.array.string_array_resolutions).size
    }

    class VideoEncResolutionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val mTextResolution: TextView

        init {
            mTextResolution = itemView.findViewById<View>(R.id.video_enc_resolution) as TextView
            itemView.setOnClickListener {
                mSelectedIdx = layoutPosition
                notifyDataSetChanged()
                val pref = PreferenceManager.getDefaultSharedPreferences(mContext)
                val editor = pref.edit()
                editor.putInt(
                    ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION,
                    mSelectedIdx
                )
                editor.apply()
            }
        }
    }

    override fun onBindViewHolder(holder: Nothing, position: Int) {
        val resolution =
            mContext!!.resources.getStringArray(R.array.string_array_resolutions)[position]
        (holder as VideoEncResolutionViewHolder).mTextResolution.text = resolution
        holder.itemView.setBackgroundResource(if (position == mSelectedIdx) R.drawable.rounded_bg_for_btn else R.drawable.rounded_bg_for_btn_normal)
        holder.mTextResolution.setTextColor(mContext!!.resources.getColor(if (position == mSelectedIdx) R.color.white else R.color.dark_black))

    }
}
