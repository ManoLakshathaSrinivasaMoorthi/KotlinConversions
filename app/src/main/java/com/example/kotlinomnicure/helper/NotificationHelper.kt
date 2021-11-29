package com.example.kotlinomnicure.helper

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.kotlinomnicure.R

class NotificationHelper(activity: Activity?, base: Context?, ): ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null
    private val PRIMARY_CHANNEL = "com.mvp.omnicure.ANDROID"
    private var smallIcon = 0
    private var largeIcon: Bitmap? = null
    private var soundUri: Uri? = null
    private var isAutoCancel = false



    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
    }

    /**
     * Registers notification channels, which can be used later by individual notifications.
     */
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

    /**
     * Get Notification builder for below oreo version
     *
     * @param title         - title
     * @param message       - message
     * @param pendingIntent - pending intent
     * @return - notification builder object for below orer version
     */
    fun getNotification(
        title: String?,
        message: String?,
        pendingIntent: PendingIntent?
    ): NotificationCompat.Builder {
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

    /**
     * To send notification
     *
     * @param pendingIntent - pending intent
     * @param title         - title of notificaiton
     * @param message       - message
     * @param id            - id of the notification
     */
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

    /**
     * Send a notification.
     *
     * @param id           The ID of the notification
     * @param notification The notification object
     */
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
