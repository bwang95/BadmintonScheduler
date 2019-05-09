package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.CourtNumberRequest
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BadmintonAPI {
  // Courts
  @GET("courts")
  fun getCourts(): Single<CourtsResponse>

  @POST("courts/register")
  fun registerCourt(@Body request: RegisterCourtRequest): Single<GenericResponse>

  @POST("courts/unregister")
  fun unregisterCourt(@Body request: CourtNumberRequest): Single<GenericResponse>

  @POST("courts/reset")
  fun resetCourt(@Body request: CourtNumberRequest): Single<GenericResponse>

  // Players
  @GET("players")
  fun getPlayers(): Single<PlayersResponse>

  @POST("players")
  fun addPlayer(@Body request: Player): Single<GenericResponse>

  @DELETE("players/{name}")
  fun removePlayer(@Path("name") name: String): Single<GenericResponse>

  // Session
  @POST("sessions")
  fun startSession(): Single<GenericResponse>

  @DELETE("sessions")
  fun endSession(): Single<GenericResponse>
}