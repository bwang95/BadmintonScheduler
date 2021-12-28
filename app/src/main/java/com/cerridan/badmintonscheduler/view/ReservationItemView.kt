package com.cerridan.badmintonscheduler.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Reservation
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.formatTime
import com.squareup.phrase.Phrase
import java.util.Date
import java.util.concurrent.TimeUnit

class ReservationItemView(
    context: Context,
    attrs: AttributeSet
): RelativeLayout(context, attrs) {
  private val namesView: TextView by bindView(R.id.tv_reservation_names)
  private val timeView: TextView by bindView(R.id.tv_reservation_time)

  fun bind(reservation: Reservation, durationMillis: Long) {
    val now = Date()

    namesView.text = if (reservation.playerNames.isEmpty()) {
      resources.getString(R.string.reservation_randoms)
    } else {
      reservation.playerNames.joinToString()
    }
    timeView.text = if (reservation.startsAt.before(now)) {
      val minutes = TimeUnit.MILLISECONDS.toMinutes(
          reservation.startsAt.time + durationMillis - now.time
      )
      Phrase.from(this, R.string.reservation_time_remaining)
          .put("minutes", minutes.toInt())
          .format()
    } else {
      Phrase.from(this, R.string.reservation_starts_at)
          .put("time", reservation.startsAt.formatTime(context))
          .format()
    }
  }
}