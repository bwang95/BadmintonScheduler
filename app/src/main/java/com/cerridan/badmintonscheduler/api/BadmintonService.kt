package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.CourtNumberRequest
import com.cerridan.badmintonscheduler.api.request.DeletePlayerRequest
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.request.UnregisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers.io
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException

class BadmintonService(
    private val retrofit: Retrofit,
    private val api: BadmintonAPI
) {
  private fun <T> Single<T>.handleErrorsOnMainThread(onErrorReturn: (Throwable) -> T): Single<T> =
      this
          .subscribeOn(io())
          .onErrorReturn { throwable ->
            val converter = retrofit.responseBodyConverter<GenericResponse>(GenericResponse::class.java, arrayOf())
            val transmuted = try {
              (throwable as? HttpException)
                  ?.response()
                  ?.errorBody()
                  ?.let { converter.convert(it)?.error?.let(::Exception) }
                  ?: throwable
            } catch (e: IOException) {
              throwable
            }

            onErrorReturn(transmuted)
          }
          .observeOn(mainThread())

  // Courts
  fun getCourts() = api
      .getCourts()
      .handleErrorsOnMainThread(::CourtsResponse)

  fun registerCourt(courtNumber: Int, players: List<Player>, delayMinutes: Int) = api
      .registerCourt(RegisterCourtRequest(courtNumber, players.map(Player::name), delayMinutes, false))
      .handleErrorsOnMainThread(::GenericResponse)

  fun unregisterCourt(reservationToken: String) = api
      .unregisterCourt(UnregisterCourtRequest(reservationToken))
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
      .removePlayer(DeletePlayerRequest(name))
      .handleErrorsOnMainThread(::GenericResponse)

  // Session
  fun startSession() = api
      .startSession(Any())
      .handleErrorsOnMainThread(::GenericResponse)

  fun endSession() = api
      .endSession()
      .handleErrorsOnMainThread(::GenericResponse)
}