package com.cerridan.badmintonscheduler.view

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat.getColor
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.TextView
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.formatTime
import com.squareup.phrase.Phrase
import java.util.Date
import java.util.concurrent.TimeUnit

class CourtItemView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
  private val numberView: TextView by bindView(R.id.tv_court_number)
  private val timeView: TextView by bindView(R.id.tv_court_time)

  @ColorInt private var textColor: Int? = null

  override fun onFinishInflate() {
    super.onFinishInflate()

    textColor = numberView.textColors.defaultColor
  }

  fun bind(number: Int, start: Date, expiry: Date) {
    val now = Date()

    numberView.text = Phrase.from(this, R.string.court_item_number)
        .put("number", number)
        .format()

    if (start.after(now)) {
      timeView.setTextColor(getColor(resources, R.color.orange_500, null))
    } else {
      textColor?.let(timeView::setTextColor)

      timeView.text = expiry.formatTime(context)
    }
    timeView.text = Phrase.from(this, R.string.court_item_time)
        .put("minutes", TimeUnit.MILLISECONDS.toMinutes(expiry.time - now.time).toInt())
        .put("time", expiry.formatTime(context))
        .format()
  }
}