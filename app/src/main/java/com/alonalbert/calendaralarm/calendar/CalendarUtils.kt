package com.alonalbert.calendaralarm.calendar

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.seconds

fun ContentResolver.register(uri: Uri): Flow<Boolean> {
  val flow = callbackFlow<Boolean> {
    val observer = object : ContentObserver(null) {
      override fun onChange(selfChange: Boolean) {
        trySend(selfChange)
      }
    }
    registerContentObserver(uri, false, observer)
    awaitClose {
      unregisterContentObserver(observer)
    }
  }
  @Suppress("OPT_IN_USAGE")
  return flow.debounce(5.seconds)
}

