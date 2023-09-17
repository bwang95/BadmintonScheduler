package com.cerridan.badmintonscheduler.api.response

import androidx.compose.runtime.Immutable
import com.cerridan.badmintonscheduler.api.model.Player
import com.squareup.moshi.Json

@Immutable
class PlayersResponse(
    @Json(name = "users") val players: List<Player>? = null,
    error: String? = null
) : GenericResponse(error) {
  constructor(error: Throwable) : this(error = error.message)
}