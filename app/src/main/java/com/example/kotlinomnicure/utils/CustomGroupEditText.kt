package com.example.kotlinomnicure.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet

import com.example.kotlinomnicure.R

@Suppress("DEPRECATION")
class CustomGroupEditText:androidx.appcompat.widget.AppCompatEditText {
    private var error = ""
    private var newPadding = 0
    private var oldPadding = paddingTop

    private fun init() {
        val scale = resources.displayMetrics.density
        newPadding = paddingTop + (3 * scale + 0.5f).toInt()
    }

    constructor(context: Context?): super(context!!){
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?): super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int):super(context!!, attrs, defStyleAttr) {
        init()
    }

    override fun draw(canvas: Canvas) {
        val p = Paint()
        p.color = resources.getColor(R.color.error_color)
        val scale = context.resources.displayMetrics.density
        val mGestureThreshold = (10f * scale + 0.5f).toInt()
        p.textSize = mGestureThreshold.toFloat()
        canvas.drawText(error, pxToDp(14).toFloat(), 30f, p)
        canvas.save()
        super.draw(canvas)
    }

    fun setErrorMessage(str: Any) {
        error = str!!
        if (str != "") {
            setPadding(paddingLeft, newPadding, paddingRight, paddingBottom)
        } else {
            setPadding(paddingLeft, oldPadding, paddingRight, paddingBottom)
        }
    }

    fun getErrorMessage(): String {
        return error
    }

    private fun pxToDp(px: Int): Int {
        return (px * Resources.getSystem().displayMetrics.density).toInt()
    }

}
