package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.CourtNumberRequest
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io

class BadmintonService(private val api: BadmintonAPI) {
  private fun <T> Single<T>.handleErrorsOnMainThread(onErrorReturn: (Throwable) -> T): Single<T> =
      this
          .subscribeOn(io())
          .onErrorReturn(onErrorReturn)
          .observeOn(mainThread())

  // Courts
  fun getCourts() = api
      .getCourts()
      .handleErrorsOnMainThread(::CourtsResponse)

  fun registerCourt(courtNumber: Int, players: List<Player>, delayMinutes: Int) = api
      .registerCourt(RegisterCourtRequest(courtNumber, players.map(Player::name), delayMinutes, false))
      .handleErrorsOnMainThread(::GenericResponse)

  fun unregisterCourt(courtNumber: Int) = api
      .unregisterCourt(CourtNumberRequest(courtNumber))
      .handleErrorsOnMainThread(::GenericResponse)

  fun resetCourt(courtNumber: Int) = api
      .resetCourt(CourtNumberRequest(courtNumber))
      .handleErrorsOnMainThread(::GenericResponse)

  // Players
  fun getPlayers() = api
      .getPlayers()
      .handleErrorsOnMainThread(::PlayersResponse)

  fun addPlayer(player: Player) = api
      .addPlayer(player)
      .handleErrorsOnMainThread(::GenericResponse)

  fun removePlayer(name: String) = api
      .removePlayer(name)
      .handleErrorsOnMainThread(::GenericResponse)

  // Session
  fun startSession() = api
      .startSession()
      .handleErrorsOnMainThread(::GenericResponse)

  fun endSession() = api
      .endSession()
      .handleErrorsOnMainThread(::GenericResponse)
}