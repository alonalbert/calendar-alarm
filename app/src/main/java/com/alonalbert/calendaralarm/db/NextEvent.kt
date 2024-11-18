package com.alonalbert.calendaralarm.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "next_event")
data class NextEvent(
  @PrimaryKey val id: Long = 0,
  val title: String = "",
  val begin: Long = -1,
  )