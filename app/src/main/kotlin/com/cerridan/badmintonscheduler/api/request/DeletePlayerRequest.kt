package com.cerridan.badmintonscheduler.api.request

import com.squareup.moshi.Json

class DeletePlayerRequest(@Json(name = "name") val name: String)