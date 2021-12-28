package com.cerridan.badmintonscheduler.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cerridan.badmintonscheduler.database.dao.PlayerDAO
import com.cerridan.badmintonscheduler.database.dao.ReservationDAO
import com.cerridan.badmintonscheduler.database.model.PlayerEntity
import com.cerridan.badmintonscheduler.database.model.ReservationEntity

@Database(
    version = 1,
    entities = [
        PlayerEntity::class,
        ReservationEntity::class
    ]
)
abstract class BadmintonDatabase : RoomDatabase() {
  companion object {
    const val DB_NAME = "badminton-database"
  }

  abstract fun playerDao(): PlayerDAO

  abstract fun reservationDao(): ReservationDAO
}
