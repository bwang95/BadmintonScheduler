package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.CourtNumberRequest
import com.cerridan.badmintonscheduler.api.request.DeletePlayerRequest
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.request.UnregisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface BadmintonAPI {
  // Courts
  @GET("courts")
  fun getCourts(): Single<CourtsResponse>

  @POST("courts/register")
  fun registerCourt(@Body request: RegisterCourtRequest): Single<GenericResponse>

  @POST("courts/unregister")
  fun unregisterCourt(@Body request: UnregisterCourtRequest): Single<GenericResponse>

  @POST("court/reset")
  fun resetCourt(@Body request: CourtNumberRequest): Single<GenericResponse>

  // Players
  @GET("players")
  fun getPlayers(): Single<PlayersResponse>

  @POST("players/add")
  fun addPlayer(@Body request: Player): Single<GenericResponse>

  @POST("players/delete")
  fun removePlayer(@Body request: DeletePlayerRequest): Single<GenericResponse>

  // Session
  @POST("session")
  fun startSession(@Body request: Any): Single<GenericResponse>

  @DELETE("session")
  fun endSession(): Single<GenericResponse>
}