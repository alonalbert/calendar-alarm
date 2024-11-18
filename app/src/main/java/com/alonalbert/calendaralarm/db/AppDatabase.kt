package com.alonalbert.calendaralarm.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [NextEvent::class],
  version = 1,
  exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun nextEventDao(): NextEventDao
}