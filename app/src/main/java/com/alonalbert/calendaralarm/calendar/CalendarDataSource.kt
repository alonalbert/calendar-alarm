package com.alonalbert.calendaralarm.calendar

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract.Attendees
import android.provider.CalendarContract.Calendars
import android.provider.CalendarContract.Events
import android.provider.CalendarContract.Instances
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import kotlin.time.Duration.Companion.days


private val PROJECTION = arrayOf(
  Instances.TITLE,
  Instances.BEGIN,
)

private const val TITLE_IDX = 0
private const val BEGIN_IDX = 1

private val EVENT_SELECTION = """
  TRUE
  AND ${Events.ALL_DAY} = 0 
  AND ${Events.SELF_ATTENDEE_STATUS} IN ( ${Events.STATUS_CONFIRMED}, ${Attendees.ATTENDEE_STATUS_NONE})  
""".trimIndent()

class CalendarDataSource(private val contentResolver: ContentResolver) {

  suspend fun getNextEvent(searchTerms: List<String>): Event? {
    val start = System.currentTimeMillis()
    val end = start + 7.days.inWholeMilliseconds
    return withContext(Dispatchers.IO) {
      val calendars = contentResolver.loadCalendars()
      val selection = buildString {
        append(EVENT_SELECTION)
        if (calendars.isNotEmpty()) {
          append(" AND (")
          append(calendars.keys.joinToString(separator = " OR ") { "${Events.CALENDAR_ID} = $it" })
          append(")")
        }
        if (searchTerms.isNotEmpty()) {
          append(" AND (")
          append(searchTerms.joinToString(separator = " OR ") { "${Events.TITLE} LIKE '%$it%'" })
          append(")")
        }
      }
      val cursor = contentResolver.queryInstances(start, end, selection) ?: return@withContext null
      cursor.use {
        when (cursor.moveToNext()) {
          true -> cursor.getEvent()
          false -> null
        }
      }
    }
  }
}

private fun Cursor.getEvent(): Event {
  val title = getString(TITLE_IDX)
  val startTime = Instant.ofEpochMilli(getLong(BEGIN_IDX))
  return Event(title, startTime)
}

private fun ContentResolver.queryInstances(start: Long, end: Long, selection: String? = null, args: List<Any>? = null): Cursor? {
  val uri = Instances.CONTENT_URI.buildUpon()
    .append(start)
    .append(end)
    .build()

  val selectionArgs = args?.map { it.toString() }?.toTypedArray()
  val fullSelection = selection + " AND ${Instances.BEGIN} > $start"
  return query(uri, PROJECTION, fullSelection, selectionArgs, Instances.BEGIN)
}

private fun ContentResolver.loadCalendars(): Map<Long, String> {
  val cursor = query(
    Calendars.CONTENT_URI,
    arrayOf(
      Calendars._ID,
      Calendars.CALENDAR_DISPLAY_NAME,
      Calendars.OWNER_ACCOUNT,
    ),
    "${Calendars.OWNER_ACCOUNT} NOT LIKE '%.calendar.google.com'",
    null,
    null
  )
  return buildMap {
    cursor?.use {
      while (cursor.moveToNext()) {
        put(cursor.getLong(0), cursor.getString(1))
      }
    }
  }
}

private fun Uri.Builder.append(long: Long) = ContentUris.appendId(this, long)