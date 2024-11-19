package com.alonalbert.calendaralarm.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alonalbert.calendaralarm.calendar.Event
import com.alonalbert.calendaralarm.utils.toLocalTimeString
import java.time.Instant

@Composable
fun MainScreen(onTriggerAlarm: () -> Unit, modifier: Modifier = Modifier) {
  val viewModel = viewModel<MainViewModel>()
  val event by viewModel.nextEventState.collectAsStateWithLifecycle()
  MainScreen(event, onTriggerAlarm, modifier)
}

@Composable
private fun MainScreen(event: Event, onTriggerAlarm: () -> Unit, modifier: Modifier = Modifier) {
  LazyColumn(modifier = modifier) {
    item {
      val text = buildString {
        append(event.title)
        if (event.begin.epochSecond > 0) {
          append(" ")
          append(event.begin.toLocalTimeString())
        }
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
