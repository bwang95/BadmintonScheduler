package com.cerridan.badmintonscheduler.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cerridan.badmintonscheduler.api.model.Player

@Entity(tableName = "players")
class PlayerEntity(
    @PrimaryKey @ColumnInfo(name = "name", index = true) val name: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "court") val court: String
) {
    val player: Player
        get() = Player(
            name = name,
            password = password,
            court = court
        )

    constructor(player: Player) : this(
        name = player.name,
        password = player.password,
        court = player.court ?: ""
    )
}
