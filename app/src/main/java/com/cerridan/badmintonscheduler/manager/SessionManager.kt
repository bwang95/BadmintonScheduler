package com.cerridan.badmintonscheduler.manager

import com.cerridan.badmintonscheduler.api.BadmintonService
import javax.inject.Inject

class SessionManager @Inject constructor(
  private val service: BadmintonService,
  private val reservationManager: ReservationManager,
  private val playerManager: PlayerManager
) {
  suspend fun endSession(): String {
    val response = service.endSession()
    playerManager.setShouldUpdate()
    reservationManager.setShouldUpdate()
    return response.error ?: ""
  }
}