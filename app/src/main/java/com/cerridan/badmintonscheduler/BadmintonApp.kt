package com.cerridan.badmintonscheduler

import android.app.Application
import com.cerridan.badmintonscheduler.dagger.DaggerInjector

class BadmintonApp : Application() {
  override fun onCreate() {
    super.onCreate()

    DaggerInjector.init(this)
  }
}