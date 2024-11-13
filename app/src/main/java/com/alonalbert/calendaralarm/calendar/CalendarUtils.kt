package com.alonalbert.calendaralarm.calendar

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import kotlinx.coroutines.flow.callbackFlow

fun ContentResolver.track(uri: Uri) = callbackFlow<Boolean> {
  val observer = object : ContentObserver(null) {
    override fun onChange(selfChange: Boolean) {
      trySend(selfChange)
    }
  }
  registerContentObserver(uri, false, observer)

  invokeOnClose {
    unregisterContentObserver(observer)
  }
}
