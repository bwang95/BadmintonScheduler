package com.cerridan.badmintonscheduler.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.manager.PlayerManager
import com.cerridan.badmintonscheduler.manager.ReservationManager
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.IN_PROGRESS
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.NOT_STARTED
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReservationsViewModel @Inject constructor(
  private val playerManager: PlayerManager,
  private val reservationManager: ReservationManager
) : ViewModel() {
  enum class RequestState { NOT_STARTED, IN_PROGRESS, SUCCESS }

  private val mutableErrors = MutableSharedFlow<String>()
  val errors: SharedFlow<String> = mutableErrors

  var requestState by mutableStateOf(NOT_STARTED)
    private set
  var availablePlayers by mutableStateOf<List<Player>?>(null)
    private set

  fun refresh() {
    viewModelScope.launch(Dispatchers.IO) {
      requestState = IN_PROGRESS

      val (error, players) = playerManager.getPlayers()
      availablePlayers = players.filter { it.court.isNullOrBlank() }
      if (error.isNotBlank()) mutableErrors.emit(error)
      requestState = NOT_STARTED
    }
  }

  fun submitReservation(courtNumber: Int, players: List<Player>, delayMinutes: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val error = reservationManager.createReservation(courtNumber, players, delayMinutes)
      requestState = if (error.isNotBlank()) {
        mutableErrors.emit(error)
        NOT_STARTED
      } else {
        SUCCESS
      }
    }
  }
}
