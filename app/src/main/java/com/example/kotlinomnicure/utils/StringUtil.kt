package com.example.kotlinomnicure.utils

import android.text.Spannable
import android.text.style.URLSpan

class StringUtil {
    fun stripUnderlines(textView: Spannable) {
        val spans = textView.getSpans(0, textView.length, URLSpan::class.java)
        for (span in spans) {
            val start = textView.getSpanStart(span)
            val end = textView.getSpanEnd(span)
            textView.removeSpan(span)
           val span = URLSpanNoUnderline(span.url)
            textView.setSpan(span, start, end, 0)
        }
    }
}
