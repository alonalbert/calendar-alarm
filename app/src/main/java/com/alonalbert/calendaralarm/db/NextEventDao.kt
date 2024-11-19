package com.alonalbert.calendaralarm.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NextEventDao {
  @Query("SELECT * FROM next_event LIMIT 1")
  fun observe(): Flow<NextEvent?>

  @Upsert
  suspend fun upsert(nextEvent: NextEvent)
}