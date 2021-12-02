package com.example.kotlinomnicure.videocall.propeller.ui

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

class RecyclerItemClickListener(context: Context?, listener: OnItemClickListener?) : OnItemTouchListener {
    private var mListener: OnItemClickListener? = listener

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onItemLongClick(view: View?, position: Int)
        fun onItemDoubleClick(view: View?, position: Int)
    }

    private var mGestureDetector: GestureDetector? = null

    private var mRv: RecyclerView? = null

    init {
        mGestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (mRv == null) {
                    return false
                }
                val child = mRv!!.findChildViewUnder(e.x, e.y)
                if (child != null && mListener != null) {
                    mListener!!.onItemClick(child, mRv!!.getChildAdapterPosition(child))
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                if (mRv == null) {
                    return
                }
                val child = mRv!!.findChildViewUnder(e.x, e.y)
                if (child != null && mListener != null) {
                    mListener!!.onItemLongClick(child, mRv!!.getChildAdapterPosition(child))
                }
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (mRv == null) {
                    return false
                }
                val child = mRv!!.findChildViewUnder(e.x, e.y)
                if (child != null && mListener != null) {
                    mListener!!.onItemDoubleClick(child, mRv!!.getChildAdapterPosition(child))
                }
                return true
            }
        })
    }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        mRv = view
        if (childView != null && mListener != null) {
            mGestureDetector!!.onTouchEvent(e)
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }


    override fun onRequestDisallowInterceptTouchEvent(b: Boolean) {}
}
