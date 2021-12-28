package com.cerridan.badmintonscheduler.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cerridan.badmintonscheduler.api.model.Reservation
import com.cerridan.badmintonscheduler.manager.ReservationManager
import com.cerridan.badmintonscheduler.util.SingleUseEvent
import javax.inject.Inject

class CourtsViewModel @Inject constructor(
  private val manager: ReservationManager
) : BaseViewModel() {
  data class Court(
    val name: String,
    val reservations: List<Reservation>
  )

  var courts by mutableStateOf<List<Court>?>(null)
    private set

  private val mutableErrors = MutableLiveData<SingleUseEvent<String>>()
  val errors: LiveData<SingleUseEvent<String>> = mutableErrors

  fun refresh() {
    manager.getReservations()
        .subscribe { (error, reservations) ->
          this.courts = reservations
              .sortedBy(Reservation::startsAt)
              .groupBy(Reservation::court)
              .toSortedMap()
              .map { (court, reservations) -> Court(court, reservations) }
          if (error.isNotBlank()) mutableErrors.postValue(SingleUseEvent(error))
        }
        .disposeOnClear()
  }
}
