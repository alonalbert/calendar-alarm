package com.alonalbert.calendaralarm

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.core.app.ServiceCompat
import com.alonalbert.calendaralarm.calendar.CalendarDataSource
import com.alonalbert.calendaralarm.calendar.register
import com.alonalbert.calendaralarm.utils.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppService : Service() {

  private val binder = LocalBinder()

  val supervisorJob = SupervisorJob()
  private val coroutineScope = CoroutineScope(supervisorJob)


  inner class LocalBinder : Binder() {
    fun getService(): AppService = this@AppService
  }

  override fun onBind(intent: Intent?): IBinder {
    Log.d(TAG, "AppService.onBind")
    return binder
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d(TAG, "AppService.onStartCommand")

    startAsForegroundService()

    coroutineScope.launch {
      updateNextAlarm()
      contentResolver.register(CalendarContract.Events.CONTENT_URI).collect {
        Log.i(TAG, "Events changed")
        updateNextAlarm()
      }
    }
    return super.onStartCommand(intent, flags, startId)
  }

  private suspend fun updateNextAlarm() {
    val dataSource = CalendarDataSource(contentResolver)
    val event = dataSource.getNextEvent(listOf("Foo", "Bar"))
    when (event) {
      null -> Log.i(TAG, "No alarm events found")
      else -> Log.i(TAG, "Next alarm: $event")
    }
  }

  override fun onCreate() {
    super.onCreate()
    Log.d(TAG, "AppService.onCreate")

    Toast.makeText(this, "Foreground Service created", Toast.LENGTH_SHORT).show()
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy")

    supervisorJob.cancel()

    Toast.makeText(this, "Foreground Service destroyed", Toast.LENGTH_SHORT).show()
  }

  /**
   * Promotes the service to a foreground service, showing a notification to the user.
   *
   * This needs to be called within 10 seconds of starting the service or the system will throw an exception.
   */
  private fun startAsForegroundService() {
    // create the notification channel
    NotificationUtils.createNotificationChannel(this)

    // promote service to foreground service
    ServiceCompat.startForeground(
      this,
      1,
      NotificationUtils.buildNotification(this),
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
      } else {
        0
      }
    )
  }
}