package com.alonalbert.calendaralarm

import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getActivity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.app.ServiceCompat
import com.alonalbert.calendaralarm.App.Companion.getDatabase
import com.alonalbert.calendaralarm.R.drawable.ic_notification
import com.alonalbert.calendaralarm.R.string.app_name
import com.alonalbert.calendaralarm.alarm.Alarm
import com.alonalbert.calendaralarm.alarm.AlarmScheduler
import com.alonalbert.calendaralarm.calendar.CalendarDataSource
import com.alonalbert.calendaralarm.calendar.Event
import com.alonalbert.calendaralarm.calendar.register
import com.alonalbert.calendaralarm.db.NextEvent
import com.alonalbert.calendaralarm.ui.MainActivity
import com.alonalbert.calendaralarm.utils.Notifications.GENERAL_NOTIFICATION_CHANNEL_ID
import com.alonalbert.calendaralarm.utils.Notifications.SERVICE_NOTIFICATION_ID
import com.alonalbert.calendaralarm.utils.toLocalTimeString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppService : Service() {
  private val supervisorJob = SupervisorJob()
  private val coroutineScope = CoroutineScope(supervisorJob)
  private val alarmScheduler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { AlarmScheduler(this) }

  override fun onBind(intent: Intent) = null

  override fun onCreate() {
    Log.d(TAG, "AppService.onCreate")
    super.onCreate()

    coroutineScope.launch {
      contentResolver.register(CalendarContract.CONTENT_URI).collect {
        Log.i(TAG, "Events changed")
        updateNextAlarm()
      }
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d(TAG, "AppService.onStartCommand")

    startAsForegroundService()

    coroutineScope.launch {
      updateNextAlarm()
    }
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy")

    supervisorJob.cancel()
  }

  /**
   * Promotes the service to a foreground service, showing a notification to the user.
   *
   * This needs to be called within 10 seconds of starting the service or the system will throw an exception.
   */
  private fun startAsForegroundService() {
    // create the notification channel

    val notification = NotificationCompat.Builder(this, GENERAL_NOTIFICATION_CHANNEL_ID)
      .setContentTitle(this.getString(app_name))
      .setSmallIcon(ic_notification)
      .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
      .setContentIntent(getActivity(this, 0, Intent(this, MainActivity::class.java), FLAG_IMMUTABLE))
      .build()

    // promote service to foreground service
    ServiceCompat.startForeground(
      this,
      SERVICE_NOTIFICATION_ID,
      notification,
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
      } else {
        0
      }
    )
  }

  private suspend fun updateNextAlarm() {
    val dataSource = CalendarDataSource(contentResolver)
    val event = dataSource.getNextEvent(listOf())
    when (event) {
      null -> cancelAlarm()
      else -> scheduleAlarm(event)
    }
  }

  private suspend fun scheduleAlarm(event: Event) {
    Log.i(TAG, "Scheduling alarm for event '${event.title}' at ${event.begin.toLocalTimeString()}")
    alarmScheduler.schedule(Alarm(event.title, event.begin))
    application.getDatabase().nextEventDao().upsert(NextEvent(0, event.title, event.begin.toEpochMilli()))
  }

  private suspend fun cancelAlarm() {
    Log.i(TAG, "No alarm events found, canceling alarm")
    alarmScheduler.cancel()
    application.getDatabase().nextEventDao().upsert(NextEvent(0, "None", -1))

  }

  companion object {
    fun start(context: Context) {
      Log.i(TAG, "Start service")
      context.startForegroundService(Intent(context, AppService::class.java))
    }
  }
}