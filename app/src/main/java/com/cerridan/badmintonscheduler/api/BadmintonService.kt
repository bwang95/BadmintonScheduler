package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.CourtNumberRequest
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse
import io.reactivex.Single

class BadmintonService(private val api: BadmintonAPI) {
  // Courts
  fun getCourts(): Single<CourtsResponse> = api.getCourts()

  fun registerCourt(request: RegisterCourtRequest): Single<GenericResponse> = api.registerCourt(request)

  fun unregisterCourt(request: CourtNumberRequest): Single<GenericResponse> = api.unregisterCourt(request)

  fun resetCourt(request: CourtNumberRequest): Single<GenericResponse> = api.resetCourt(request)

  // Players
  fun getPlayers(): Single<PlayersResponse> = api.getPlayers()

  fun addPlayer(player: Player): Single<GenericResponse> = api.addPlayer(player)

  fun removePlayer(name: String): Single<GenericResponse> = api.removePlayer(name)

  // Session
  fun startSession(): Single<GenericResponse> = api.startSession()

  fun endSession(): Single<GenericResponse> = api.endSession()
}