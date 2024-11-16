package com.alonalbert.calendaralarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import com.alonalbert.calendaralarm.utils.Notifications.GENERAL_NOTIFICATION_CHANNEL_ID
import com.alonalbert.calendaralarm.utils.Notifications.ALARM_NOTIFICATION_CHANNEL_ID

class App : Application() {
  override fun onCreate() {
    super.onCreate()

    createNotificationChannels()
  }

  private fun createNotificationChannels() {
    val notificationManager = getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(
      NotificationChannel(
        GENERAL_NOTIFICATION_CHANNEL_ID,
        getString(R.string.general_channel_name),
        IMPORTANCE_DEFAULT
      )
    )
    notificationManager.createNotificationChannel(
      NotificationChannel(
        ALARM_NOTIFICATION_CHANNEL_ID,
        getString(R.string.alarm_channel_name),
        IMPORTANCE_HIGH
      )
    )
  }
}