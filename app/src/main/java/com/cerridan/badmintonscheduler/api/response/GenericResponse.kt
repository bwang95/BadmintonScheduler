package com.cerridan.badmintonscheduler.api.response

import androidx.compose.runtime.Immutable
import com.squareup.moshi.Json

@Immutable
open class GenericResponse(@Json(name = "error") val error: String? = null) {
  constructor(throwable: Throwable) : this(throwable.message)
}