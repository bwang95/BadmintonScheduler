package com.cerridan.badmintonscheduler.api.request

import com.squareup.moshi.Json

class UnregisterCourtRequest(@Json(name = "token") val reservationToken: String)