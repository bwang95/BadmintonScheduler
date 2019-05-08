package com.cerridan.badmintonscheduler.dagger

import android.app.Activity
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ActivityModule(private val activity: Activity) {
  @Provides @Singleton fun provideMoshi() =
      Moshi.Builder()
          .build()
}