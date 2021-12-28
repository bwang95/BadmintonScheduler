package com.cerridan.badmintonscheduler.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

private val TWENTY_FOUR_HOUR_FORMAT = object : ThreadLocal<SimpleDateFormat>() {
  @SuppressLint("SimpleDateFormat")
  override fun initialValue() = SimpleDateFormat("HH:mm")
}

private val TWELVE_HOUR_FORMAT = object : ThreadLocal<SimpleDateFormat>() {
  @SuppressLint("SimpleDateFormat")
  override fun initialValue() = SimpleDateFormat("h:mm a")
}

fun Date.formatTime(context: Context) =
    if (DateFormat.is24HourFormat(context)) TWENTY_FOUR_HOUR_FORMAT.get()!!.format(this)
    else TWELVE_HOUR_FORMAT.get()!!.format(this)