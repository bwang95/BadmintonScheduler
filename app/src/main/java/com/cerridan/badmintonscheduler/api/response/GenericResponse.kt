package com.cerridan.badmintonscheduler.api.response

import com.squareup.moshi.Json

open class GenericResponse(@Json(name = "error") val error: String? = null) {
  constructor(throwable: Throwable) : this(throwable.message)
}