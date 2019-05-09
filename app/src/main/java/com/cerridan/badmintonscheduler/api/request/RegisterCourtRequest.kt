package com.cerridan.badmintonscheduler.api.request

import com.squareup.moshi.Json

class RegisterCourtRequest(
    @Json(name = "court_number") val number: Int,
    @Json(name = "names") val names: List<String>,
    @Json(name = "delay_time_minutes") val delayTime: Int,
    @Json(name = "randoms") val isRandoms: Boolean
)