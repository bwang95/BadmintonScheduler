package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BadmintonAPI {
  // Courts
  @GET("courts")
  fun getCourts(): Single<CourtsResponse>

  @POST("courts")
  fun registerCourt(@Body request: RegisterCourtRequest): Single<GenericResponse>

  @DELETE("courts/reset/{id}")
  fun resetCourt(@Path("id") courtNumber: String): Single<GenericResponse>

  @DELETE("courts/{id}")
  fun unregisterCourt(@Path("id") reservationToken: String): Single<GenericResponse>

  // Players
  @GET("users")
  fun getPlayers(): Single<PlayersResponse>

  @POST("users")
  fun addPlayer(@Body request: Player): Single<GenericResponse>

  @DELETE("users/{id}")
  fun removePlayer(@Path("id") name: String): Single<GenericResponse>

  // Session
  @POST("sessions")
  fun startSession(@Body request: Any): Single<GenericResponse>

  @DELETE("sessions/1")
  fun endSession(): Single<GenericResponse>
}