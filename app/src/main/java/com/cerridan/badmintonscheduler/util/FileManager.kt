package com.cerridan.badmintonscheduler.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileNotFoundException

class FileManager(private val context: Context, private val gson: Gson) {

  fun write(filename: String, writable: Any): Unit =
      File(context.filesDir, filename).writeText(gson.toJson(writable))

  fun <T> read(filename: String, type: Class<T>): T? = try {
    gson.fromJson(File(context.filesDir, filename).readText(), type)
  } catch (e: FileNotFoundException) {
    null
  }
}