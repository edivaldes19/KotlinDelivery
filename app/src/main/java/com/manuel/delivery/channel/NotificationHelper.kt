package com.manuel.delivery.channel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.manuel.delivery.R

class NotificationHelper(context: Context) : ContextWrapper(context) {
    private var notificationManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                packageName,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.WHITE
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            getManager().createNotificationChannel(notificationChannel)
        }
    }

    fun getManager(): NotificationManager {
        if (notificationManager == null) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager as NotificationManager
    }

    fun getNotification(title: String, body: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, packageName).setContentTitle(title)
            .setContentText(body).setAutoCancel(true).setColor(Color.GRAY)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
    }
}