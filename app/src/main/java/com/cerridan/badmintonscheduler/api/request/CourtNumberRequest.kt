package com.cerridan.badmintonscheduler.api.request

import com.squareup.moshi.Json

class CourtNumberRequest(
    @Json(name = "court_number") val number: Int
)