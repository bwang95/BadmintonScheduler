package com.cerridan.badmintonscheduler.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cerridan.badmintonscheduler.database.model.ReservationEntity

@Dao
interface ReservationDAO {
    @Query("SELECT ends_at FROM reservations ORDER BY ends_at ASC LIMIT 1")
    fun getEarliestReservationEndTime(): Long?

    @Query("SELECT * FROM reservations")
    fun getReservations(): List<ReservationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReservations(reservations: List<ReservationEntity>)

    @Query("DELETE FROM reservations WHERE id NOT IN (:existingReservations)")
    fun deleteOldReservations(existingReservations: List<String>)
}
