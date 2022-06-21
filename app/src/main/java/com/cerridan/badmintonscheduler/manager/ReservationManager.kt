package com.cerridan.badmintonscheduler.manager

import androidx.annotation.WorkerThread
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.model.Reservation
import com.cerridan.badmintonscheduler.database.dao.ReservationDAO
import com.cerridan.badmintonscheduler.database.model.ReservationEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class ReservationManager @Inject constructor(
  private val badmintonService: BadmintonService,
  private val reservationDao: ReservationDAO
) {
  companion object {
    val COURT_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(45)
    private val UPDATE_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(5)
  }

  private var lastUpdate = AtomicReference(Date(0))

  fun getReservations(forceUpdate: Boolean = false): Single<Pair<String, List<Reservation>>> = Single
      .fromCallable { Date(reservationDao.getEarliestReservationEndTime() ?: 0) }
      .flatMap { expiration ->
        val now = Date()
        if (forceUpdate || expiration < now || lastUpdate.get() < Date(now.time - UPDATE_INTERVAL_MILLIS)) {
          badmintonService.getCourts()
              .observeOn(Schedulers.io())
              .doOnSuccess { lastUpdate.set(now) }
              .map {
                updateReservationDatabase(it.courts ?: emptyList())
                (it.error ?: "") to reservationDao.getReservations()
              }
        } else {
          Single.just("" to reservationDao.getReservations())
        }
      }
      .map { (error, reservations) -> error to reservations.map(ReservationEntity::reservation) }
      .subscribeOn(Schedulers.io())

  fun createReservation(courtNumber: Int, players: List<Player>, delayMinutes: Int) =
    badmintonService.registerCourt(courtNumber, players, delayMinutes)

  fun deleteReservation(token: String): Single<String> = badmintonService
      .unregisterCourt(token)
      .doOnSuccess { lastUpdate.set(Date(0)) }
      .map { it.error ?: "" }

  @WorkerThread
  private fun updateReservationDatabase(reservations: List<Reservation>) {
    reservationDao.insertReservations(reservations.map(::ReservationEntity))
    reservationDao.deleteOldReservations(reservations.map(Reservation::token))
  }
}