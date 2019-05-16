package com.cerridan.badmintonscheduler.api.response

import com.cerridan.badmintonscheduler.api.model.Reservation
import com.squareup.moshi.Json

class CourtsResponse(
    @Json(name = "reservations") val courts: List<Reservation>? = null,
    error: String? = null
) : GenericResponse(error) {
  constructor(error: Throwable) : this(error = error.message)
}