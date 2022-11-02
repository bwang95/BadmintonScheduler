package com.cerridan.badmintonscheduler.api.model

import androidx.compose.runtime.Immutable
import com.squareup.moshi.Json

@Immutable
data class Player(
    @Json(name = "name") val name: String,
    @Json(name = "password") val password: String,
    @Json(name = "court") val court: String? = null
)
