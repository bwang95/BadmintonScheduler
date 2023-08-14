package com.cerridan.badmintonscheduler.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cerridan.badmintonscheduler.database.model.PlayerEntity

@Dao
interface PlayerDAO {
  @Query("SELECT * FROM players")
  fun getPlayers(): List<PlayerEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertPlayers(players: List<PlayerEntity>)

  @Query("DELETE FROM players WHERE name NOT IN (:names)")
  fun deleteNonexistentPlayers(names: List<String>)
}
