package com.cerridan.badmintonscheduler.api.model

import com.squareup.moshi.Json
import java.util.Date

class Reservation(
    @Json(name = "id") val token: String,
    @Json(name = "court") val court: String,
    @Json(name = "users") val playerNames: List<String>,
    @Json(name = "starts_at") val startsAt: Date,
    @Json(name = "ends_at") val endsAt: Date
)
