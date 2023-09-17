package com.cerridan.badmintonscheduler.manager

import android.util.Log
import androidx.annotation.WorkerThread
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.database.dao.PlayerDAO
import com.cerridan.badmintonscheduler.database.model.PlayerEntity
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class PlayerManager @Inject constructor(
  private val badmintonService: BadmintonService,
  private val playerDao: PlayerDAO
) {
  companion object {
    private val UPDATE_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(5)
  }

  private var lastUpdate = AtomicReference(Date(0))

  suspend fun addPlayer(player: Player): String {
    val response = badmintonService.addPlayer(player)
    setShouldUpdate()
    return response.error ?: ""
  }

  suspend fun removePlayer(name: String): String {
    val response = badmintonService.removePlayer(name)
    setShouldUpdate()
    return response.error ?: ""
  }

  suspend fun getPlayers(forceUpdate: Boolean = false): Pair<String, List<Player>> {
    val error = if (forceUpdate || lastUpdate.get() < Date(Date().time - UPDATE_INTERVAL_MILLIS)) {
      val response = badmintonService.getPlayers()
      lastUpdate.set(Date())
      response.players?.let(::updatePlayerDatabase)
      response.error ?: ""
    } else {
      ""
    }

    return error to playerDao.getPlayers().map(PlayerEntity::player)
  }

  fun setShouldUpdate() {
    lastUpdate.set(Date(0))
  }

  @WorkerThread
  fun updatePlayerDatabase(players: List<Player>) {
    playerDao.deleteNonexistentPlayers(players.map(Player::name))
    playerDao.insertPlayers(players.map(::PlayerEntity))
  }
}