package com.cerridan.badmintonscheduler.dagger

import android.app.Activity
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.BadmintonAPI
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton

@Module
class ActivityModule(private val activity: Activity) {
  @Provides @Singleton fun provideMoshi() =
      Moshi.Builder()
          .build()

  @Provides @Singleton fun provideOkHttpClient() =
      OkHttpClient.Builder()
          .build()

  @Provides @Singleton fun provideRetrofit(client: OkHttpClient) =
      Retrofit.Builder()
          .baseUrl(activity.getString(R.string.api_base_url))
          .client(client)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .build()

  @Provides @Singleton fun provideBadmintonApi(retrofit: Retrofit) =
      retrofit.create(BadmintonAPI::class.java)

  @Provides @Singleton fun provideBadmintonService(api: BadmintonAPI) =
      BadmintonService(api)
}