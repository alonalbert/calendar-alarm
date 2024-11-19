package com.alonalbert.calendaralarm.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_CALENDAR
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.alonalbert.calendaralarm.AppService
import com.alonalbert.calendaralarm.alarm.AlarmBroadcastReceiver
import com.alonalbert.calendaralarm.ui.theme.CalendarAlarmTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    checkAndRequestPermissions()

    enableEdgeToEdge()
    setContent {
      CalendarAlarmTheme {
        Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
          MainScreen({ AlarmBroadcastReceiver.triggerAlarm(this, "Test") }, modifier = Modifier.Companion.padding(innerPadding))
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
    val permissions = buildList {
      add(READ_CALENDAR)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(POST_NOTIFICATIONS)
      }
    }
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

      when {
        results[READ_CALENDAR] == true -> AppService.start(this)
        else -> {} // Toast.makeText(this, "Calendar permission is required!", LENGTH_SHORT).show()
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
