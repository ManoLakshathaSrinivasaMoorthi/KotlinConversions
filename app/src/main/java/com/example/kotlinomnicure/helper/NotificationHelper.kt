package com.example.kotlinomnicure.helper

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.kotlinomnicure.R

class NotificationHelper(base: Context?): ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null
    private val PRIMARY_CHANNEL = "com.mvp.omnicure.ANDROID"
    private var smallIcon = 0
    private var largeIcon: Bitmap? = null
    private var soundUri: Uri? = null
    private var isAutoCancel = false

    constructor(context: Context, nothing: Nothing?) :this(context){

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        smallIcon = R.drawable.ic_notification
        largeIcon =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_color)
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        isAutoCancel = true
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        //returning the same icons
//        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
        return R.mipmap.ic_launcher
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    fun registerNotificationChannel() {
        @SuppressLint("WrongConstant") val chan1 = NotificationChannel(
            PRIMARY_CHANNEL,
            getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_HIGH
        )
        chan1.lightColor = Color.GREEN
        chan1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        getManager()!!.createNotificationChannel(chan1)
    }

    /**
     * Get the notification manager.
     *
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private fun getManager(): NotificationManager? {
        if (notificationManager == null) {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }


    private fun getNotification(title: String?, message: String?, pendingIntent: PendingIntent?): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL)
            .setSmallIcon(smallIcon)
            .setLargeIcon(largeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(isAutoCancel)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
    }


    fun sendNotification(pendingIntent: PendingIntent?, title: String?, message: String?, id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerNotificationChannel()
        }
        val notificationBuilder = getNotification(title, message, pendingIntent)
        notfiy(id, notificationBuilder)
    }


    fun setAutoCancel(isAutoCancel: Boolean) {
        this.isAutoCancel = isAutoCancel
    }

    fun setSoundUri(soundUri: Uri?) {
        this.soundUri = soundUri
    }

    fun setLargeIcon(largeIcon: Bitmap?) {
        this.largeIcon = largeIcon
    }


    private fun notfiy(id: Int, notification: NotificationCompat.Builder) {
        getManager()!!.notify(id, notification.build())
    }

    fun clearNotification(notify_id: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notify_id)
    }

    fun clearAllNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}
