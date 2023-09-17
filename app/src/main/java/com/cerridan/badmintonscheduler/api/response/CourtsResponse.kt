package com.cerridan.badmintonscheduler.api.response

import androidx.compose.runtime.Immutable
import com.cerridan.badmintonscheduler.api.model.Reservation
import com.squareup.moshi.Json

@Immutable
class CourtsResponse(
    @Json(name = "courts") val courts: List<Reservation>? = null,
    error: String? = null
) : GenericResponse(error) {
  constructor(error: Throwable) : this(error = error.message)
}