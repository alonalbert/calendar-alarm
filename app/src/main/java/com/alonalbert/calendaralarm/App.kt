package com.alonalbert.calendaralarm

import android.app.Application
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MIN
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MAX
import androidx.room.Room
import com.alonalbert.calendaralarm.db.AppDatabase
import com.alonalbert.calendaralarm.utils.Notifications.ALARM_NOTIFICATION_CHANNEL_ID
import com.alonalbert.calendaralarm.utils.Notifications.GENERAL_NOTIFICATION_CHANNEL_ID

class App : Application() {
  private val database by lazy {
    Room.databaseBuilder(
      applicationContext,
      AppDatabase::class.java,
      "app-database.db"
    ).build()
  }

  override fun onCreate() {
    super.onCreate()

    createNotificationChannels()
  }

  private fun createNotificationChannels() {
    val notificationManager = NotificationManagerCompat.from(applicationContext)
    val generalChannel = NotificationChannelCompat.Builder(GENERAL_NOTIFICATION_CHANNEL_ID, IMPORTANCE_MIN)
      .setName(getString(R.string.general_channel_name))
      .setSound(null, null)
      .build()
    val alarmChannel = NotificationChannelCompat.Builder(ALARM_NOTIFICATION_CHANNEL_ID, IMPORTANCE_MAX)
      .setName(getString(R.string.alarm_channel_name))
      .setSound(null, null)
      .build()
    notificationManager.createNotificationChannelsCompat(listOf(generalChannel, alarmChannel))
  }

  companion object {
    fun Application.getDatabase() = (this as App).database
  }
}