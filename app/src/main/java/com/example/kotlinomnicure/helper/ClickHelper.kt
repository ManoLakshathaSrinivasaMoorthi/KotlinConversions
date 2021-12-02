package com.example.kotlinomnicure.helper

import android.os.Handler
import android.view.View

class ClickHelper {
    fun handleMultipleClick(view: View) {
        view.isEnabled = false
        view.isClickable = false
        Handler().postDelayed({ view.isClickable = true
            view.isEnabled = true
        }, 500)
    }

}