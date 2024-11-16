package com.alonalbert.calendaralarm.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.alonalbert.calendaralarm.R
import com.alonalbert.calendaralarm.TAG
import com.alonalbert.calendaralarm.ui.MainActivity
import com.alonalbert.calendaralarm.utils.Notifications.ALARM_NOTIFICATION_CHANNEL_ID
import com.alonalbert.calendaralarm.utils.Notifications.ALARM_NOTIFICATION_ID

private const val REQUEST_CODE_TRIGGER = 10
private const val REQUEST_CODE_DISMISS = 11
private const val REQUEST_CODE_FULL_SCREEN_NOTIFICATION = 12


private const val INTENT_FLAGS = FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT

class AlarmBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    when (intent.action) {
      ACTION_TRIGGER -> context.triggerAlarm(intent)
      ACTION_DISMISS -> context.dismissAlarm()
      else -> {}
    }
  }

  private fun Context.triggerAlarm(intent: Intent) {
    val title = intent.getStringExtra(TITLE)
    Log.i(TAG, "AlarmBroadcastReceiver: Alarm '$title' triggered")
    val notificationManager = getSystemService(NotificationManager::class.java)

    // TODO: Make this open event in calendar
    val fullScreenIntent = PendingIntent.getActivity(
      applicationContext,
      REQUEST_CODE_FULL_SCREEN_NOTIFICATION,
      Intent(this, MainActivity::class.java),
      INTENT_FLAGS,
    )

    val notification = NotificationCompat.Builder(applicationContext, ALARM_NOTIFICATION_CHANNEL_ID)
      .setContentTitle(title)
      .setSmallIcon(R.drawable.ic_alarm)
      .setShowWhen(true)
      .setContentIntent(fullScreenIntent)
      .setFullScreenIntent(fullScreenIntent, true)
      .setColor(ContextCompat.getColor(applicationContext, R.color.purple_500))
      .addAction(R.drawable.ic_close, getString(R.string.dismiss), createDismiss(this))
      .setOngoing(true)
      .build()

    notificationManager.notify(1, notification)
  }

  private fun Context.dismissAlarm() {
    Log.i(TAG, "AlarmBroadcastReceiver: Dismiss")
    getSystemService(NotificationManager::class.java).cancel("tag", ALARM_NOTIFICATION_ID)
  }

  companion object {
    const val TITLE = "TITLE"
    const val ACTION_TRIGGER = "ACTION_TRIGGER"
    const val ACTION_DISMISS = "ACTION_DISMISS"

    fun createTrigger(context: Context, alarm: Alarm? = null): PendingIntent {
      val intent = Intent(context, AlarmBroadcastReceiver::class.java).setAction(ACTION_TRIGGER).apply {
        if (alarm != null) {
          putExtra(TITLE, alarm.title)
        }
      }
      return PendingIntent.getBroadcast(
        context,
        REQUEST_CODE_TRIGGER,
        intent,
        INTENT_FLAGS,
      )
    }

    fun createDismiss(context: Context): PendingIntent {
      val intent = Intent(context, AlarmBroadcastReceiver::class.java).setAction(ACTION_DISMISS)
      return PendingIntent.getBroadcast(
        context,
        REQUEST_CODE_DISMISS,
        intent,
        INTENT_FLAGS,
      )
    }

  }
}