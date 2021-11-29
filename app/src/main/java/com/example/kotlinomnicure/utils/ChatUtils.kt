package com.example.kotlinomnicure.utils

import android.text.format.DateFormat
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class ChatUtils {

    private val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS


    fun getTimeAgo(time: Long): String? {
        if (1 == 1) {
            return getStatusDateFormat(time)
        }
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = time
        val now = Calendar.getInstance()
        val timeFormatString = "hh:mm aa"
        val dateTimeFormatString = "EEEE, MMMM d, hh:mm a"
        val HOURS = (60 * 60 * 60).toLong()
        return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
            //            return "Today " + DateFormat.format(timeFormatString, smsTime);
            "Today " + DateFormat.format(timeFormatString, smsTime)
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
            "Yesterday " + DateFormat.format(timeFormatString, smsTime)
        } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else {
            DateFormat.format("MMMM dd yyyy, hh:mm aa", smsTime).toString()
        }

    }

    fun getStatusDateFormat(milliSeconds: Long): String? {
        try {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = milliSeconds
            val now = Calendar.getInstance()
            val date = Date(milliSeconds)
            var dateFormat = ""
            var prefix = ""
            if (DateUtils.isToday(date.time)) {
                prefix = "Today "
                dateFormat = "hh:mma"
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                prefix = "Yesterday "
                dateFormat = "hh:mma"
            } else {
                dateFormat = "MMM dd, yyyy hh:mma"
            }
            val formatter = SimpleDateFormat(dateFormat, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getDefault()
            var dateString = formatter.format(Date(milliSeconds))
            dateString = prefix + dateString
            return dateString.replace("AM", "am").replace("PM", "pm")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getDateFormat(milliSeconds: Long, dateFormat: String?): String? {
        try {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = milliSeconds
            val now = Calendar.getInstance()
            val date = Date(milliSeconds)
            val formatter = SimpleDateFormat(dateFormat, Locale.ENGLISH)
            //            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            return formatter.format(Date(milliSeconds))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getDateFormat(milliSeconds: Long): String? {
        return getDateFormat(milliSeconds, "dd MMM yyyy")
    }





}