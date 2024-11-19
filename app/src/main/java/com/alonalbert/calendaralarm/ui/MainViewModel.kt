package com.alonalbert.calendaralarm.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alonalbert.calendaralarm.App.Companion.getDatabase
import com.alonalbert.calendaralarm.calendar.Event
import com.alonalbert.calendaralarm.db.NextEvent
import com.alonalbert.calendaralarm.utils.stateIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant

private val LOADING = Event("Loading...", Instant.MIN)
private val NONE = Event("None", Instant.MIN)

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val nextEventDao = application.getDatabase().nextEventDao()
  val nextEventState: StateFlow<Event> = nextEventDao.observe().map { it?.toEvent() ?: NONE }.stateIn(viewModelScope, LOADING)
}

private fun NextEvent.toEvent() = Event(title, Instant.ofEpochMilli(begin))