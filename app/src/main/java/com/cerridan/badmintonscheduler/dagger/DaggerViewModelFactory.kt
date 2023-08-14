package com.cerridan.badmintonscheduler.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cerridan.badmintonscheduler.viewmodel.CourtsViewModel
import com.cerridan.badmintonscheduler.viewmodel.PlayersViewModel
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Provider

class DaggerViewModelFactory @Inject constructor(
  courtsViewModelProvider: Provider<CourtsViewModel>,
  playersViewModelProvider: Provider<PlayersViewModel>,
  reservationsViewModelProvider: Provider<ReservationsViewModel>
) : ViewModelProvider.Factory {
  private val providers: Map<Class<*>, Provider<out ViewModel>> = mapOf(
      CourtsViewModel::class.java to courtsViewModelProvider,
      PlayersViewModel::class.java to playersViewModelProvider,
      ReservationsViewModel::class.java to reservationsViewModelProvider
  )

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return (providers[modelClass]?.get() as? T)
        ?: throw IllegalArgumentException("${modelClass.name} not provided in DaggerViewModelFactory")
  }
}