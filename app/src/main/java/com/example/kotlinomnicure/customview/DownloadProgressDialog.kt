package com.example.kotlinomnicure.customview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import java.lang.Exception

class DownloadProgressDialog(context: Context) : Dialog(context) {
    private val TAG = "DownLoadProg"
    private var activity: Activity? = null

    fun DownloadProgressDialog(activity: Activity?) {

      // super(activity)
        this.activity = activity
        setContentView(R.layout.download_progress)
        getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlmp: WindowManager.LayoutParams? = getWindow()?.getAttributes()
        val width = (getContext().getResources().getDisplayMetrics().widthPixels * 0.75) as Int
        wlmp?.width = width
        wlmp?.gravity = Gravity.CENTER
        getWindow()?.setAttributes(wlmp)
        setTitle(null)
        setCancelable(false)
    }

    override fun show() {
        try {
            var isDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed = activity!!.isDestroyed
            }
            if (!activity!!.isFinishing && !isDestroyed && !this.isShowing()) {
                super.show()
            }
        } catch (e: Exception) {

        }
    }

    fun setText(text: String?) {
        val textView: TextView = findViewById<TextView>(R.id.id_pb_txt)
        textView.text = text
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int) {
        val percentTxt: TextView = findViewById<TextView>(R.id.id_percent)
        percentTxt.text = "$progress%"
    }

    fun setCancelBtnListener(onClickListener: View.OnClickListener?) {
        val cancelBtn: Button = findViewById<Button>(R.id.cancel)
        cancelBtn.setOnClickListener(onClickListener)
    }
}