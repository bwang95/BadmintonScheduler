package com.cerridan.badmintonscheduler.dagger

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.cerridan.badmintonscheduler.BuildConfig
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.BadmintonAPI
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.UnixDateAdapter
import com.cerridan.badmintonscheduler.database.BadmintonDatabase
import com.cerridan.badmintonscheduler.database.dao.PlayerDAO
import com.cerridan.badmintonscheduler.database.dao.ReservationDAO
import com.cerridan.badmintonscheduler.manager.PlayerManager
import com.cerridan.badmintonscheduler.manager.ReservationManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
  @Provides @Singleton fun provideMoshi(): Moshi =
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

  @Provides @Singleton fun provideRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit =
      Retrofit.Builder()
          .baseUrl(app.getString(R.string.api_base_url))
          .client(client)
          .addConverterFactory(MoshiConverterFactory.create(moshi))
          .build()

  @Provides @Singleton fun provideBadmintonApi(retrofit: Retrofit): BadmintonAPI =
      retrofit.create(BadmintonAPI::class.java)

  @Provides @Singleton fun provideBadmintonService(retrofit: Retrofit, api: BadmintonAPI) =
      BadmintonService(retrofit, api)

  @Provides @Singleton fun provideBadmintonDatabase(): BadmintonDatabase =
      Room.databaseBuilder(app, BadmintonDatabase::class.java, BadmintonDatabase.DB_NAME).build()

  @Provides @Singleton fun providePlayerDao(database: BadmintonDatabase) =
      database.playerDao()

  @Provides @Singleton fun provideReservationDao(database: BadmintonDatabase) =
      database.reservationDao()

  @Provides @Singleton fun provideReservationManager(
    service: BadmintonService,
    playerManager: PlayerManager,
    dao: ReservationDAO
  ) = ReservationManager(service, playerManager, dao)

  @Provides @Singleton fun providePlayerManager(service: BadmintonService, dao: PlayerDAO) =
      PlayerManager(service, dao)
}
