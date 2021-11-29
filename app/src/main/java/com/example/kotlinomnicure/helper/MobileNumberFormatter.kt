package com.example.kotlinomnicure.helper

import android.text.TextUtils
import android.widget.EditText

class MobileNumberFormatter {
    private var prevLength = 0
    private var isAlter = false

    fun formatMobileNumber(mobileNo: String, editText: EditText, firstBlockLength: Int, secondBlockLength: Int) {
        try {
            if (TextUtils.isEmpty(mobileNo.trim { it <= ' ' })) {
                return
            }
            val currLength = mobileNo.length
            if (currLength == firstBlockLength && prevLength == currLength - 1 && !isAlter) {
                isAlter = true
                prevLength = currLength
                val result = mobileNo.substring(0, firstBlockLength - 1) + "-" + mobileNo.substring(firstBlockLength - 1)
                editText.setText(result)
                editText.setSelection(editText.text.length)
                return
            } else if (currLength == firstBlockLength && prevLength == currLength + 1 && !isAlter) {
                val result = mobileNo.substring(0, mobileNo.length - 1)
                editText.setText(result)
                editText.setSelection(editText.text.length)
                return
            } else if (currLength == secondBlockLength && prevLength == currLength - 1 && !isAlter) {
                isAlter = true
                prevLength = currLength
                val result = mobileNo.substring(0, secondBlockLength - 1) + "-" + mobileNo.substring(secondBlockLength - 1)
                editText.setText(result)
                editText.setSelection(editText.text.length)
                return
            } else if (currLength > firstBlockLength && currLength == secondBlockLength && prevLength == currLength + 1 && !isAlter) {
                val result = mobileNo.substring(0, mobileNo.length - 1)
                editText.setText(result)
                editText.setSelection(editText.text.length)
                return
            } else {
                isAlter = false
            }
            prevLength = currLength
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


