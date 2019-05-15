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
  @GET("court")
  fun getCourts(): Single<CourtsResponse>

  @POST("court/register")
  fun registerCourt(@Body request: RegisterCourtRequest): Single<GenericResponse>

  @POST("court/unregister")
  fun unregisterCourt(@Body request: CourtNumberRequest): Single<GenericResponse>

  @POST("court/reset")
  fun resetCourt(@Body request: CourtNumberRequest): Single<GenericResponse>

  // Players
  @GET("player")
  fun getPlayers(): Single<PlayersResponse>

  @POST("player")
  fun addPlayer(@Body request: Player): Single<GenericResponse>

  @DELETE("player/{name}")
  fun removePlayer(@Path("name") name: String): Single<GenericResponse>

  // Session
  @POST("session")
  fun startSession(): Single<GenericResponse>

  @DELETE("session")
  fun endSession(): Single<GenericResponse>
}