package com.cerridan.badmintonscheduler.manager

import com.cerridan.badmintonscheduler.api.BadmintonService
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SessionManager @Inject constructor(
  private val service: BadmintonService,
  private val reservationManager: ReservationManager,
  private val playerManager: PlayerManager
) {
  fun endSession(): Single<String> = service
    .endSession()
    .doOnSuccess {
      playerManager.setShouldUpdate()
      reservationManager.setShouldUpdate()
    }
    .map { response -> response.error ?: "" }
}