package com.example.kotlinomnicure.customview

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.widget.TextView
import com.example.kotlinomnicure.R


class CustomProgressDialog(context: Activity) : Dialog(context) {
    private var activity: Activity? = context

    init {
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER
        window!!.attributes = wlmp
        setTitle(null)
        setCancelable(false)
        setContentView(R.layout.custom_progress)
    }


    override fun show() {
        try {
            val isDestroyed: Boolean = activity!!.isDestroyed
            if (!activity!!.isFinishing && !isDestroyed && !this.isShowing) {
                super.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setText(text: String?) {
        val textView: TextView = findViewById(R.id.id_pb_txt)
        textView.text = text
    }
}


