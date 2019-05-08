package com.cerridan.badmintonscheduler.model

import com.google.gson.annotations.SerializedName
import java.util.*
import java.util.concurrent.TimeUnit

data class Session(
    @SerializedName("id") val id: Long,
    @SerializedName("players") val players: List<Player>,
    @SerializedName("starts_at") val startsAt: Date,
    @SerializedName("length_minutes") val lengthMins: Long
) {
  val endsAt get() = Date(startsAt.time + TimeUnit.MINUTES.toMillis(lengthMins))

  override fun equals(other: Any?) = (other as? Player)?.id == id
}