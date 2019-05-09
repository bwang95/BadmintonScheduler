package com.cerridan.badmintonscheduler.api.response

import com.squareup.moshi.Json

open class GenericResponse(
    @Json(name = "error") val error: String? = null
) {
  val isSuccess get() = error == null

  constructor(throwable: Throwable) : this(throwable.message)
}