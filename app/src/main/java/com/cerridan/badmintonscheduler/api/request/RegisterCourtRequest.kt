package com.cerridan.badmintonscheduler.api.request

import com.squareup.moshi.Json

class RegisterCourtRequest(
    @Json(name = "courtNumber") val number: Int,
    @Json(name = "players") val names: List<String>,
    @Json(name = "delayInMinutes") val delayTime: Int,
    @Json(name = "randoms") val isRandoms: Boolean
)