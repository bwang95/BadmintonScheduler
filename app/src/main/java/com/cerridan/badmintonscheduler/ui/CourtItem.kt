package com.cerridan.badmintonscheduler.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.manager.ReservationManager
import com.cerridan.badmintonscheduler.util.formatTime
import com.cerridan.badmintonscheduler.viewmodel.CourtsViewModel.Court
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun CourtItem(
  modifier: Modifier = Modifier,
  court: Court,
  now: Date
) = Column(modifier) {
  Row(
      modifier = Modifier.padding(
          horizontal = dimensionResource(R.dimen.global_padding_half),
          vertical = dimensionResource(R.dimen.global_padding)
      )
  ) {
    Text(
        modifier = Modifier.weight(1f),
        text = stringResource(R.string.court_item_number, court.name)
    )

    val expiry = court.reservations.last().endsAt
    val minutes = TimeUnit.MILLISECONDS.toMinutes(expiry.time - now.time)
    Text(stringResource(R.string.court_item_time, minutes, expiry.formatTime(LocalContext.current)))
  }
  court.reservations.forEach { reservation ->
    Divider(startIndent = dimensionResource(R.dimen.global_padding))
    Row(
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.global_padding),
            end = dimensionResource(R.dimen.global_padding_half),
            top = dimensionResource(R.dimen.global_padding),
            bottom = dimensionResource(R.dimen.global_padding)
        )
    ) {
      Text(
          modifier = Modifier.weight(1f),
          text = reservation.playerNames.joinToString()
      )

      val time = if (reservation.startsAt.before(now)) {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(
            reservation.startsAt.time + ReservationManager.COURT_DURATION_MILLIS - now.time
        )
        stringResource(R.string.reservation_time_remaining, minutes)
      } else {
        stringResource(R.string.reservation_starts_at, reservation.startsAt.formatTime(LocalContext.current))
      }
      Text(time)
    }
  }
}
