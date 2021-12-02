package com.example.kotlinomnicure.videocall.propeller.ui

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.LinearLayout

class AGLinearLayout: LinearLayout {

    constructor(context: Context?) :super(context)


    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    constructor(context: Context?, attrs: AttributeSet?) :super(context, attrs)


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return ViewUtil().checkDoubleTouchEvent(event, this) || super.dispatchTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return ViewUtil().checkDoubleKeyEvent(event, this) || super.dispatchKeyEvent(event)
    }
}

