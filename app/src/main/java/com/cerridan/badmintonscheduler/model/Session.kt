package com.cerridan.badmintonscheduler.model

import com.squareup.moshi.Json
import java.util.Date
import java.util.concurrent.TimeUnit

data class Session(
    @Json(name = "id") val id: Long,
    @Json(name = "players") val players: List<Player>,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "length_minutes") val lengthMins: Long
) {
  val endsAt get() = Date(startsAt.time + TimeUnit.MINUTES.toMillis(lengthMins))

  override fun equals(other: Any?) = (other as? Player)?.id == id
}