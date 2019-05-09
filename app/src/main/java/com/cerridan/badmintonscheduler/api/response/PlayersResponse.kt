package com.cerridan.badmintonscheduler.api.response

import com.cerridan.badmintonscheduler.api.model.Player
import com.squareup.moshi.Json

class PlayersResponse(
    @Json(name = "players") val players: List<Player>
) : GenericResponse()