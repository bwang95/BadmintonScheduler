package com.cerridan.badmintonscheduler

import android.app.Application
import android.os.Build
import android.support.v7.app.AppCompatDelegate
import android.support.v7.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES
import com.cerridan.badmintonscheduler.dagger.DaggerInjector

class BadmintonApp : Application() {
  override fun onCreate() {
    super.onCreate()

    AppCompatDelegate.setDefaultNightMode(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) MODE_NIGHT_FOLLOW_SYSTEM
        else MODE_NIGHT_YES
    )

    DaggerInjector.init(this)
  }
}