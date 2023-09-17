package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException

class BadmintonService(
    private val retrofit: Retrofit,
    private val api: BadmintonAPI
) {
  private suspend fun <T> callAndConvertErrors(
    output: (Throwable) -> T,
    call: suspend () -> T
  ): T = try {
    call()
  } catch(throwable: Throwable) {
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

    output(transmuted)
  }

  // Courts
  suspend fun getCourts() = callAndConvertErrors(::CourtsResponse, api::getCourts)

  suspend fun registerCourt(courtNumber: Int, players: List<Player>, delayMinutes: Int) =
    callAndConvertErrors(::GenericResponse) {
      api.registerCourt(RegisterCourtRequest(courtNumber, players.map(Player::name), delayMinutes))
    }

  suspend fun unregisterCourt(reservationToken: String) =
    callAndConvertErrors(::GenericResponse) { api.unregisterCourt(reservationToken) }

  suspend fun resetCourt(courtNumber: String) =
    callAndConvertErrors(::GenericResponse) { api.resetCourt(courtNumber) }

  // Players
  suspend fun getPlayers() = callAndConvertErrors(::PlayersResponse, api::getPlayers)

  suspend fun addPlayer(player: Player) =
    callAndConvertErrors(::GenericResponse) { api.addPlayer(player) }

  suspend fun removePlayer(name: String) =
    callAndConvertErrors(::GenericResponse) { api.removePlayer(name) }

  // Session
  suspend fun endSession() = callAndConvertErrors(::GenericResponse, api::endSession)
}