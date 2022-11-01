package com.cerridan.badmintonscheduler.manager

import androidx.annotation.WorkerThread
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.database.dao.PlayerDAO
import com.cerridan.badmintonscheduler.database.model.PlayerEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
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

  fun addPlayer(player: Player): Single<String> = badmintonService
      .addPlayer(player)
      .doOnSuccess { setShouldUpdate() }
      .map { response -> response.error ?: "" }

  fun removePlayer(name: String): Single<String> = badmintonService
      .removePlayer(name)
      .doOnSuccess { setShouldUpdate() }
      .map { response -> response.error ?: "" }

  fun getPlayers(forceUpdate: Boolean = false): Single<Pair<String, List<Player>>> = Single
      .fromCallable { forceUpdate || lastUpdate.get() < Date(Date().time - UPDATE_INTERVAL_MILLIS) }
      .flatMap { shouldUpdate ->
        if (shouldUpdate) {
          badmintonService.getPlayers()
              .observeOn(Schedulers.io())
              .doOnSuccess { lastUpdate.set(Date()) }
              .map {
                it.players?.let(::updatePlayerDatabase)
                (it.error ?: "") to playerDao.getPlayers()
              }
        } else {
          Single.just("" to playerDao.getPlayers())
        }
      }
      .map { (error, players) -> error to players.map(PlayerEntity::player) }
      .subscribeOn(Schedulers.io())

  fun setShouldUpdate() {
    lastUpdate.set(Date(0))
  }

  @WorkerThread
  fun updatePlayerDatabase(players: List<Player>) {
    playerDao.deleteNonexistentPlayers(players.map(Player::name))
    playerDao.insertPlayers(players.map(::PlayerEntity))
  }
}