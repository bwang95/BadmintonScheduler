package com.cerridan.badmintonscheduler.api

import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.api.request.RegisterCourtRequest
import com.cerridan.badmintonscheduler.api.response.CourtsResponse
import com.cerridan.badmintonscheduler.api.response.GenericResponse
import com.cerridan.badmintonscheduler.api.response.PlayersResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BadmintonAPI {
  // Courts
  @GET("courts")
  suspend fun getCourts(): CourtsResponse

  @POST("courts")
  suspend fun registerCourt(@Body request: RegisterCourtRequest): GenericResponse

  @DELETE("courts/reset/{id}")
  suspend fun resetCourt(@Path("id") courtNumber: String): GenericResponse

  @DELETE("courts/{id}")
  suspend fun unregisterCourt(@Path("id") reservationToken: String): GenericResponse

  // Players
  @GET("users")
  suspend fun getPlayers(): PlayersResponse

  @POST("users")
  suspend fun addPlayer(@Body request: Player): GenericResponse

  @DELETE("users/{id}")
  suspend fun removePlayer(@Path("id") name: String): GenericResponse

  // Session
  @DELETE("sessions/1")
  suspend fun endSession(): GenericResponse
}