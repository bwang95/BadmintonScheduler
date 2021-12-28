package com.cerridan.badmintonscheduler.api.request

import com.squareup.moshi.Json

class RegisterCourtRequest(
    @Json(name = "court") val number: Int,
    @Json(name = "users") val names: List<String>,
    @Json(name = "starts_in_minutes") val delayTime: Int,
)
