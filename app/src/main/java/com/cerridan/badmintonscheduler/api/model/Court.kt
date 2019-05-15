package com.cerridan.badmintonscheduler.api.model

import com.squareup.moshi.Json
import java.util.Date

data class Court(
    @Json(name = "court_number") val number: Int,
    @Json(name = "reservations") val registrations: List<Registration>
) {
  data class Registration(
      @Json(name = "start_time_seconds") val startTime: Date,
      @Json(name = "names") val players: List<String>
  )
}