package com.alonalbert.calendaralarm.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_CALENDAR
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.alonalbert.calendaralarm.AppService
import com.alonalbert.calendaralarm.R
import com.alonalbert.calendaralarm.TAG
import com.alonalbert.calendaralarm.alarm.AlarmBroadcastReceiver.Companion.createDismiss
import com.alonalbert.calendaralarm.ui.theme.CalendarAlarmTheme
import com.alonalbert.calendaralarm.utils.Notifications.ALARM_NOTIFICATION_CHANNEL_ID

class MainActivity : ComponentActivity() {
  // needed to communicate with the service.
  private val connection = object : ServiceConnection {

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
      // we've bound to ExampleLocationForegroundService, cast the IBinder and get ExampleLocationForegroundService instance.
      Log.d(TAG, "onServiceConnected")
    }

    override fun onServiceDisconnected(arg0: ComponentName) {
      // This is called when the connection with the service has been disconnected. Clean up.
      Log.d(TAG, "onServiceDisconnected")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      CalendarAlarmTheme {
        Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
          App(modifier = Modifier.Companion.padding(innerPadding))
        }
      }
    }

    checkAndRequestPermissions()
  }

  override fun onDestroy() {
    super.onDestroy()
    unbindService(connection)
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
        results[READ_CALENDAR] == true -> startForegroundService()
        else -> {} // Toast.makeText(this, "Calendar permission is required!", LENGTH_SHORT).show()
      }
    }.launch(permissions.toTypedArray())

  }

  /**
   * Creates and starts the ExampleLocationForegroundService as a foreground service.
   *
   * It also tries to bind to the service to update the UI with location updates.
   */
  private fun startForegroundService() {
    // start the service
    startForegroundService(Intent(this, AppService::class.java))
  }
}

@Composable
fun App(modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    Text("Get Calendars")
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
