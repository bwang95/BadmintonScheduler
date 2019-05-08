package com.cerridan.badmintonscheduler.model

import com.google.gson.annotations.SerializedName

data class Court(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
) {
  override fun equals(other: Any?) = (other as? Player)?.id == id
}