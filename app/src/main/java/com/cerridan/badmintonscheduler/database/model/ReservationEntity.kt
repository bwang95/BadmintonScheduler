package com.cerridan.badmintonscheduler.database.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cerridan.badmintonscheduler.api.model.Reservation
import java.util.Date

@Immutable
@Entity(tableName = "reservations")
class ReservationEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "court", index = true) val court: String,
    @ColumnInfo(name = "users") val playerNames: String,
    @ColumnInfo(name = "starts_at") val startsAt: Long,
    @ColumnInfo(name = "ends_at") val endsAt: Long
) {
    val reservation: Reservation
        get() = Reservation(
            token = id,
            court = court,
            playerNames = playerNames.split(","),
            startsAt = Date(startsAt),
            endsAt = Date(endsAt)
        )

    constructor(reservation: Reservation) : this(
        id = reservation.token,
        court = reservation.court,
        playerNames = reservation.playerNames.joinToString(separator = ","),
        startsAt = reservation.startsAt.time,
        endsAt = reservation.endsAt.time
    )
}
