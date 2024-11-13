package com.alonalbert.calendaralarm

import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract.Events
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alonalbert.calendaralarm.ui.theme.CalendarAlarmTheme
import java.util.Date

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    contentResolver.registerContentObserver(Events.CONTENT_URI, true, object : ContentObserver(Handler(mainLooper)) {
      override fun onChange(selfChange: Boolean, uris: MutableCollection<Uri>, flags: Int) {
        Log.i("Alon", "onChange: uris: ${uris.joinToString { it.toString() }}")
        getEvents()
      }
    })
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
      if (it) {
        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        getEvents()
      } else {
        Toast.makeText(this, "Please allow this app to access your calendar", Toast.LENGTH_SHORT).show()
      }
    }.launch("android.permission.READ_CALENDAR")

    enableEdgeToEdge()
    setContent {
      CalendarAlarmTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          App(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }

  @Composable
  fun App(modifier: Modifier = Modifier) {
    Button(onClick = { getEvents() }, modifier = modifier) {
      Text("Get Calendars")
    }
  }

  private fun getEvents() {
    val uri = Events.CONTENT_URI
    val cursor =
      contentResolver.query(
        uri,
        arrayOf(Events.TITLE, Events.DTSTART),
        "${Events.TITLE} LIKE '%foo%' ",
        /* selectionArgs = */        null,
        /* sortOrder = */        null
      ) ?: return

    cursor.use {
      while (cursor.moveToNext()) {
        val title = cursor.getString(0)
        val startTime = Date(cursor.getLong(1))
        Log.i("Alon", "Event: $title $startTime")
      }
    }
  }
}
