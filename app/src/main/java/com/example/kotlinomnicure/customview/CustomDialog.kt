package com.example.kotlinomnicure.customview

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.kotlinomnicure.R

class CustomDialog(context: Context?) : Dialog(context!!) {
    private var positive: Button? = null
    private  var negative: Button? = null
    private var titleMsgView: TextView? = null
    private  var msgView:TextView? = null
    private var mTitleId = -1
    private var title: String? = null
    private var msg: String? = null
    private var positivetextId = -1
    private var positiveListener: View.OnClickListener? = null
    private var negativeTextId = -1
    private var negetiveListener: View.OnClickListener? = null
    var positiveButtonColor = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        window?.setDimAmount(1f)

//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.dimAmount=0.7f;
//        getWindow().setAttributes(lp);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        titleMsgView = findViewById(R.id.id_custom_dialog_title_text)
        msgView = findViewById(R.id.id_custom_dialog_msg_text)
        val width = (context.resources.displayMetrics.widthPixels * 0.75).toInt()
        msgView!!.layoutParams.width = width
        positive = findViewById(R.id.id_custom_dialog_positive_btn)
        negative = findViewById(R.id.id_custom_dialog_negative_btn)
        val line: View = findViewById(R.id.id_line)
        if (mTitleId != -1) {
            titleMsgView?.setText(mTitleId)
        }
        if (title != null) {
            titleMsgView?.text = title
        }
        if (msg != null) {
            msgView?.text = msg
        }
        if (positivetextId != -1) {
            positive?.setText(positivetextId)
            positive?.setOnClickListener(positiveListener)
            positive?.visibility = View.VISIBLE
        }
        if (negativeTextId != -1) {
            negative?.setText(negativeTextId)
            negative?.setOnClickListener(negetiveListener)
            negative?.visibility = View.VISIBLE
        }
        if (positivetextId == -1 || negativeTextId == -1) {
            //line.setVisibility(View.GONE);
            positive?.setBackgroundResource(R.drawable.signin_selector_btn_bg)
            negative?.setBackgroundResource(R.drawable.signin_selector_btn_bg)
        }
        if (positiveButtonColor != -1) {
            positive?.setTextColor(positiveButtonColor)
        }
        //customDialogView.setVisibility(View.VISIBLE);
    }

    fun setMessage(msg: String?) {
        this.msg = msg
    }

    override fun setTitle(id: Int) {
        mTitleId = id
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setPositiveButton(textId: Int, positiveListener: View.OnClickListener?) {
        positivetextId = textId
        this.positiveListener = positiveListener
    }

    fun setNegativeButton(textId: Int, positiveListener: View.OnClickListener?) {
        negetiveListener = positiveListener
        negativeTextId = textId
    }

    fun setPositiveButtonDrawable(drawable: Int) {
        positive!!.background = getContext().getResources().getDrawable(drawable)
    }

    @JvmName("getPositiveButtonColor1")
    fun getPositiveButtonColor(): Int {
        return positiveButtonColor
    }

    @JvmName("setPositiveButtonColor1")
    fun setPositiveButtonColor(positiveButtonColor: Int) {
        this.positiveButtonColor = positiveButtonColor
    }
}


