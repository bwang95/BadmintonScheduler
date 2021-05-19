package com.cerridan.badmintonscheduler.dagger

import android.app.Application
import android.util.Log
import com.cerridan.badmintonscheduler.BuildConfig
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.BadmintonAPI
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.UnixDateAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
  @Provides @Singleton fun provideMoshi() =
      Moshi.Builder()
          .add(KotlinJsonAdapterFactory())
          .add(Date::class.java, UnixDateAdapter())
          .build()

  @Provides @Singleton fun provideOkHttpClient() =
      OkHttpClient.Builder()
          .addInterceptor(
              HttpLoggingInterceptor { Log.d("OkHttp", it) }
                  .setLevel(if (BuildConfig.DEBUG) BODY else NONE)
          )
          .build()

  @Provides @Singleton fun provideRetrofit(moshi: Moshi, client: OkHttpClient) =
      Retrofit.Builder()
          .baseUrl(app.getString(R.string.api_base_url))
          .client(client)
          .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
          .addConverterFactory(MoshiConverterFactory.create(moshi))
          .build()

  @Provides @Singleton fun provideBadmintonApi(retrofit: Retrofit) =
      retrofit.create(BadmintonAPI::class.java)

  @Provides @Singleton fun provideBadmintonService(retrofit: Retrofit, api: BadmintonAPI) =
      BadmintonService(retrofit, api)
}