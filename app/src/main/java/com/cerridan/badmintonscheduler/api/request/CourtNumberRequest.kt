package com.cerridan.badmintonscheduler.api.request

import com.squareup.moshi.Json

class CourtNumberRequest(@Json(name = "courtNumber") val number: Int)