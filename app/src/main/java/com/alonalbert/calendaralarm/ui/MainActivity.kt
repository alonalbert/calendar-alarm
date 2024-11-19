package com.alonalbert.calendaralarm.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_CALENDAR
import android.app.AlarmManager
import android.content.Intent
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.AlarmManagerCompat
import com.alonalbert.calendaralarm.AppService
import com.alonalbert.calendaralarm.TAG
import com.alonalbert.calendaralarm.alarm.AlarmBroadcastReceiver
import com.alonalbert.calendaralarm.ui.theme.CalendarAlarmTheme
import com.alonalbert.calendaralarm.utils.isAtLeast

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    checkAndRequestPermissions()

    enableEdgeToEdge()
    setContent {
      CalendarAlarmTheme {
        Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
          MainScreen({ AlarmBroadcastReceiver.triggerAlarm(this, "Test") }, { AppService.start(this) }, modifier = Modifier.Companion.padding(innerPadding))
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
  }

  /**
   * Check for notification permission before starting the service so that the notification is visible
   */
  private fun checkAndRequestPermissions() {
    if (isAtLeast(TIRAMISU)) {
      if (!AlarmManagerCompat.canScheduleExactAlarms(getSystemService(AlarmManager::class.java))) {
        // TODO: Register AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED receiver and handle UI
        startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
      }
    }
    val permissions = buildList {
      add(READ_CALENDAR)
      if (isAtLeast(TIRAMISU)) {
        add(POST_NOTIFICATIONS)
      }
    }
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
      if (results.count { it.value } == permissions.size) {
        AppService.start(this)
      } else {
        // TODO: Update UI
        Log.e(TAG, "Required permissions not granted")
      }
    }.launch(permissions.toTypedArray())

  }
}

//  private fun getEvents() {
//    val uri = CalendarContract.Events.CONTENT_URI
//    val cursor =
//      contentResolver.query(
//        uri,
//        arrayOf(CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART),
//        "${CalendarContract.Events.TITLE} LIKE '%foo%' ",
//        /* selectionArgs = */        null,
//        /* sortOrder = */        null
//      ) ?: return
//
//    cursor.use {
//      while (cursor.moveToNext()) {
//        val title = cursor.getString(0)
//        val startTime = Date(cursor.getLong(1))
//        Log.i("Alon", "Event: $title $startTime")
//      }
//    }
//  }
