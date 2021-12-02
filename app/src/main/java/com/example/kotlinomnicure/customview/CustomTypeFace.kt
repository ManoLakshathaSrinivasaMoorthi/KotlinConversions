package com.example.kotlinomnicure.customview

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.util.TypedValue
import com.example.kotlinomnicure.R

class CustomTypeFace: TypefaceSpan {

    private var newType: Typeface? = null
    private var mContext: Context? = null

    constructor(context: Context?, family: String?, type: Typeface?):super(family) {
        newType = type
        mContext = context
    }

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface?) {
        val oldStyle: Int
        val old = paint.typeface
        oldStyle = old?.style ?: 0
        val fake = oldStyle and tf!!.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }
        paint.textSize = mContext?.resources?.getInteger(R.integer.config_navAnimTime)?.toFloat()?.let {
            convertSpToPixels(it, mContext).toFloat()
        }!!
        paint.typeface = tf
    }

    private fun convertSpToPixels(sp: Float, context: Context?): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context!!.resources.displayMetrics).toInt()
    }
}
