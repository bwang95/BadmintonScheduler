package com.cerridan.badmintonscheduler.api.model

import com.squareup.moshi.Json

data class Player(
    @Json(name = "name") val name: String,
    @Json(name = "password") val password: String,
    @Json(name = "courtNumber") val courtNumber: Int? = null
)