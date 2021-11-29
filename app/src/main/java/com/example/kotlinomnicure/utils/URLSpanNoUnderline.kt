package com.example.kotlinomnicure.utils

import android.text.TextPaint
import android.text.style.URLSpan

class URLSpanNoUnderline(url: String?) : URLSpan(url) {


    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false //setting this true will underline the text content.
    }
}
