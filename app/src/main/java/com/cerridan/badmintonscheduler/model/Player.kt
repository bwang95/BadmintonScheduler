package com.cerridan.badmintonscheduler.model

import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String
) {
  override fun equals(other: Any?) = (other as? Player)?.id == id
}