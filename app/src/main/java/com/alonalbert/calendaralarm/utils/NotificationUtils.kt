package com.alonalbert.calendaralarm.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.alonalbert.calendaralarm.R
import com.alonalbert.calendaralarm.ui.MainActivity

object NotificationUtils {
  private const val NOTIFICATION_CHANNEL_ID = "general_notification_channel"

  fun createNotificationChannel(context: Context) {
    val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

    // create the notification channel
    val channel = NotificationChannel(
      NOTIFICATION_CHANNEL_ID,
      context.getString(R.string.channel_name),
      NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationManager.createNotificationChannel(channel)
  }

  fun buildNotification(context: Context): Notification {
    return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
      .setContentTitle(context.getString(R.string.app_name))
      .setSmallIcon(R.drawable.ic_notification)
      .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
      .setContentIntent(Intent(context, MainActivity::class.java).let { notificationIntent ->
        PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
      })
      .build()
  }

}