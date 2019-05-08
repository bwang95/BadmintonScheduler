package com.cerridan.badmintonscheduler.model

import com.squareup.moshi.Json

data class Player(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "password") val password: String
) {
  override fun equals(other: Any?) = (other as? Player)?.id == id
}