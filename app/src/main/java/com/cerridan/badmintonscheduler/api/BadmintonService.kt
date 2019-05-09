package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.CourtNumberRequest
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse

class BadmintonService(private val api: BadmintonAPI) {
  // Courts
  fun getCourts() = api.getCourts().onErrorReturn(::CourtsResponse)

  fun registerCourt(request: RegisterCourtRequest) =
      api.registerCourt(request).onErrorReturn(::GenericResponse)

  fun unregisterCourt(request: CourtNumberRequest) =
      api.unregisterCourt(request).onErrorReturn(::GenericResponse)

  fun resetCourt(request: CourtNumberRequest) =
      api.resetCourt(request).onErrorReturn(::GenericResponse)

  // Players
  fun getPlayers() = api.getPlayers().onErrorReturn(::PlayersResponse)

  fun addPlayer(player: Player) = api.addPlayer(player).onErrorReturn(::GenericResponse)

  fun removePlayer(name: String) = api.removePlayer(name).onErrorReturn(::GenericResponse)

  // Session
  fun startSession() = api.startSession().onErrorReturn(::GenericResponse)

  fun endSession() = api.endSession().onErrorReturn(::GenericResponse)
}