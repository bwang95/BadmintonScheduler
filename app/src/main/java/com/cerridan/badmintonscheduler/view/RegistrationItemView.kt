package com.cerridan.badmintonscheduler.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Court.Registration
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.formatTime
import com.squareup.phrase.Phrase
import java.util.Date
import java.util.concurrent.TimeUnit

class RegistrationItemView(
    context: Context,
    attrs: AttributeSet
): RelativeLayout(context, attrs) {
  private val namesView: TextView by bindView(R.id.tv_registration_names)
  private val timeView: TextView by bindView(R.id.tv_registration_time)

  fun bind(registration: Registration, durationMillis: Long) {
    val now = Date()

    namesView.text = registration.players.joinToString()
    timeView.text = if (registration.startTime.before(now)) {
      val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - registration.startTime.time + durationMillis)
      Phrase.from(this, R.string.registration_time_remaining)
          .put("minutes", minutes.toInt())
          .format()
    } else {
      Phrase.from(this, R.string.registration_starts_at)
          .put("time", registration.startTime.formatTime(context))
          .format()
    }
  }
}