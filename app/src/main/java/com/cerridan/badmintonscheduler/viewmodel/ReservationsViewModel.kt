package com.cerridan.badmintonscheduler.viewmodel

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.manager.PlayerManager
import com.cerridan.badmintonscheduler.manager.ReservationManager
import com.cerridan.badmintonscheduler.util.SingleUseEvent
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.IN_PROGRESS
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.NOT_STARTED
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.SUCCESS
import javax.inject.Inject

class ReservationsViewModel @Inject constructor(
  private val playerManager: PlayerManager,
  private val reservationManager: ReservationManager
) : BaseViewModel() {
  enum class RequestState { NOT_STARTED, IN_PROGRESS, SUCCESS }

  private val mutableErrors = MutableLiveData<SingleUseEvent<String>>()
  val errors: LiveData<SingleUseEvent<String>> = mutableErrors

  var requestState by mutableStateOf(NOT_STARTED)
    private set
  var availablePlayers by mutableStateOf<List<Player>?>(null)
    private set

  fun refresh() {
    playerManager.getPlayers()
      .doOnSubscribe { requestState = IN_PROGRESS }
      .subscribe { (error, players) ->
        this.availablePlayers = players.filter { it.court.isNullOrBlank() }
        if (error.isNotBlank()) mutableErrors.postValue(SingleUseEvent(error))
        requestState = NOT_STARTED
      }
      .disposeOnClear()
  }

  fun submitReservation(courtNumber: Int, players: List<Player>, delayMinutes: Int) {
    reservationManager.createReservation(courtNumber, players, delayMinutes)
      .subscribe { response ->
        requestState = response.error?.let {
          mutableErrors.postValue(SingleUseEvent(it))
          NOT_STARTED
        } ?: SUCCESS
      }
      .disposeOnClear()
  }
}