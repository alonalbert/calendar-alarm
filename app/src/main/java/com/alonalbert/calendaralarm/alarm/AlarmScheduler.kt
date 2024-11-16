package com.alonalbert.calendaralarm.alarm

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.alonalbert.calendaralarm.TAG
import com.alonalbert.calendaralarm.utils.toLocalTimeString

class AlarmScheduler(context: Context) {
  private val applicationContext = context.applicationContext

  private val alarmManager = context.getSystemService(AlarmManager::class.java)

  fun schedule(alarm: Alarm) {
    val ok = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms() else true
    if (ok) {
      alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        alarm.time.toEpochMilli(),
        AlarmBroadcastReceiver.createTrigger(applicationContext, alarm),
      )
      Log.i(TAG, "Scheduled alarm for ${alarm.time.toLocalTimeString()}")
    }
  }

  fun cancel() {
    Log.i(TAG, "Canceled alarm")
    alarmManager.cancel(AlarmBroadcastReceiver.createTrigger(applicationContext))
  }
}