package com.example.kotlinomnicure.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan

class TextSpanBuilder {

    fun getPartialBoldText(str: String?, start: Int, end: Int): SpannableStringBuilder? {
        if (TextUtils.isEmpty(str)) {
            return null
        }
        val builder = SpannableStringBuilder(str)
        val styleSpan = StyleSpan(Typeface.BOLD)
        builder.setSpan(styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return builder
    }

    fun getSubscriptString(str: String?, start: Int, end: Int): SpannableStringBuilder? {
        if (TextUtils.isEmpty(str)) {
            return null
        }
        val PROPORTION = 1.0f
        val builder = SpannableStringBuilder(str)
        val size = RelativeSizeSpan(PROPORTION)
        builder.setSpan(size, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return builder
    }
}
