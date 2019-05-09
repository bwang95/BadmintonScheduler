package com.cerridan.badmintonscheduler.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.util.Date
import java.util.concurrent.TimeUnit

class UnixDateAdapter : JsonAdapter<Date>() {
  override fun fromJson(reader: JsonReader) =
      Date().apply { time = TimeUnit.SECONDS.toMillis(reader.nextLong()) }

  override fun toJson(writer: JsonWriter, value: Date?) {
    value?.also { writer.value(TimeUnit.MILLISECONDS.toSeconds(it.time)) } ?: writer.nullValue()
  }
}