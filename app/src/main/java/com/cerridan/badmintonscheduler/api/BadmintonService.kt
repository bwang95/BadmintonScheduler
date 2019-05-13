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

  fun registerCourt(request: RegisterCourtRequest) = api
      .registerCourt(request)
      .handleErrorsOnMainThread(::GenericResponse)

  fun unregisterCourt(request: CourtNumberRequest) = api
      .unregisterCourt(request)
      .handleErrorsOnMainThread(::GenericResponse)

  fun resetCourt(request: CourtNumberRequest) = api
      .resetCourt(request)
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