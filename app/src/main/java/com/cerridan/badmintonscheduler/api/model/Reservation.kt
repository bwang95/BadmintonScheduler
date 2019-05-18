package com.cerridan.badmintonscheduler.api.model

import com.squareup.moshi.Json
import java.util.Date

class Reservation(
    @Json(name = "token") val token: String,
    @Json(name = "courtNumber") val courtNumber: Int,
    @Json(name = "players") val playerNames: List<String>,
    @Json(name = "startAt") val startsAt: Date,
    @Json(name = "randoms") val randoms: Boolean
)