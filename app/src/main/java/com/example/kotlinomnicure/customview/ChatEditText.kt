package com.example.kotlinomnicure.customview

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import com.example.kotlinomnicure.activity.ChatActivity
import com.example.kotlinomnicure.interfaces.ChatEditTextListener


class ChatEditText : androidx.appcompat.widget.AppCompatEditText {
    private var chatEditTextListener: ChatEditTextListener? = null

    constructor(context: Context?) :super(context!!){
        chatEditTextListener = context as ChatActivity?
    }

    constructor(context: Context?, attrs: AttributeSet?):super(context!!, attrs) {
        chatEditTextListener = context as ChatActivity?
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int):   super(context!!, attrs, defStyle) {
        chatEditTextListener = context as ChatActivity?
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
//        Log.d("ChatEditText", "onKeyPreIme: " + keyCode + ", " + event);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            chatEditTextListener!!.onBackButtonPressed()
            return true
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        Log.d("ChatEditText", "onTouchEvent: " + event);
        val isPopupShown = chatEditTextListener!!.onKeyboardTouch()
        return if (isPopupShown) {
            false
        } else super.onTouchEvent(event)
    }

}
