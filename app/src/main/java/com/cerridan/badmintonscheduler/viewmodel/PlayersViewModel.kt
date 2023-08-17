package com.cerridan.badmintonscheduler.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.manager.PlayerManager
import com.cerridan.badmintonscheduler.util.SingleUseEvent
import javax.inject.Inject

class PlayersViewModel @Inject constructor(
  private val playerManager: PlayerManager
) : BaseViewModel() {
  private val mutableErrors = MutableLiveData<SingleUseEvent<String>>()
  val errors: LiveData<SingleUseEvent<String>> get() = mutableErrors

  var players by mutableStateOf<List<Player>?>(null)
    private set
  var isLoading by mutableStateOf(false)
    private set

  fun refresh(forceUpdate: Boolean = false) {
    playerManager.getPlayers(forceUpdate)
        .doOnSubscribe { isLoading = true }
        .doOnEvent { _, _ -> isLoading = false }
        .subscribe { (error, players) ->
          this.players = players
          if (error.isNotBlank()) mutableErrors.postValue(SingleUseEvent(error))
        }
        .disposeOnClear()
  }
}
