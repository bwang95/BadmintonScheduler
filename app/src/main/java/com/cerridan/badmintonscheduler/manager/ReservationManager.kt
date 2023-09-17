package com.cerridan.badmintonscheduler.manager

import android.util.Log
import androidx.annotation.WorkerThread
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.model.Reservation
import com.cerridan.badmintonscheduler.database.dao.ReservationDAO
import com.cerridan.badmintonscheduler.database.model.ReservationEntity
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class ReservationManager @Inject constructor(
  private val badmintonService: BadmintonService,
  private val playerManager: PlayerManager,
  private val reservationDao: ReservationDAO
) {
  companion object {
    val COURT_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(30)
    private val UPDATE_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(5)
  }

  private var lastUpdate = AtomicReference(Date(0))

  suspend fun getReservations(forceUpdate: Boolean = false): Pair<String, List<Reservation>> {
    val now = Date()
    val expiration = Date(reservationDao.getEarliestReservationEndTime() ?: 0)
    val updateTime = Date(now.time - UPDATE_INTERVAL_MILLIS)

    val error = if (forceUpdate || expiration < now || lastUpdate.get() < updateTime) {
      val response = badmintonService.getCourts()
      lastUpdate.set(now)
      updateReservationDatabase(response.courts ?: emptyList())
      response.error ?: ""
    } else {
      ""
    }

    return error to reservationDao.getReservations().map(ReservationEntity::reservation)
  }

  suspend fun resetCourt(courtNumber: String): String {
    val response = badmintonService.resetCourt(courtNumber)
    setShouldUpdate()
    return response.error ?: ""
  }

  suspend fun createReservation(
    courtNumber: Int,
    players: List<Player>,
    delayMinutes: Int
  ): String {
    val response = badmintonService.registerCourt(courtNumber, players, delayMinutes)
    setShouldUpdate()
    return response.error ?: ""
  }

  suspend fun deleteReservation(token: String): String {
    val response = badmintonService.unregisterCourt(token)
    setShouldUpdate()
    return response.error ?: ""
  }

  fun setShouldUpdate() {
    playerManager.setShouldUpdate()
    lastUpdate.set(Date(0))
  }

  @WorkerThread
  private fun updateReservationDatabase(reservations: List<Reservation>) {
    reservationDao.insertReservations(reservations.map(::ReservationEntity))
    reservationDao.deleteOldReservations(reservations.map(Reservation::token))
  }
}