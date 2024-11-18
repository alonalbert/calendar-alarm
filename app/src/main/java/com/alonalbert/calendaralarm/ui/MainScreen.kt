package com.alonalbert.calendaralarm.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alonalbert.calendaralarm.calendar.Event
import com.alonalbert.calendaralarm.utils.toLocalTimeString
import java.time.Instant

@Composable
fun MainScreen(event: Event?, onTriggerAlarm: () -> Unit, modifier: Modifier = Modifier) {
  LazyColumn(modifier = modifier) {
    item {
      val text = when (event) {
        null -> "None"
        else -> "${event.title} ${event.begin.toLocalTimeString()}"
      }
      Text("Next event: $text")
    }
    item {
      Button(onClick = onTriggerAlarm) {
        Text("Trigger Alarm")
      }
    }
  }
}

@Preview
@Composable
fun AppPreview() {
  MainScreen(Event("Test", Instant.now()), {})
}
