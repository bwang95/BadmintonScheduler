package com.cerridan.badmintonscheduler.dagger

import android.app.Activity
import com.cerridan.badmintonscheduler.model.DataModel
import com.cerridan.badmintonscheduler.util.FileManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ActivityModule(private val activity: Activity) {
  @Provides @Singleton fun provideGson() = Gson()

  @Provides @Singleton fun provideFileManager(gson: Gson) = FileManager(activity, gson)

  @Provides @Singleton fun provideDataModel(fileManager: FileManager) =
      fileManager.read(DataModel.FILE_NAME, DataModel::class.java) ?: DataModel()
}