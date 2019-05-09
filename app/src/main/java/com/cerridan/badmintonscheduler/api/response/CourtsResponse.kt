package com.cerridan.badmintonscheduler.api.response

import com.cerridan.badmintonscheduler.api.model.Court
import com.squareup.moshi.Json

class CourtsResponse(
    @Json(name = "courts") val courts: List<Court>? = null,
    error: String? = null
) : GenericResponse(error) {
  constructor(error: Throwable) : this(error = error.message)
}