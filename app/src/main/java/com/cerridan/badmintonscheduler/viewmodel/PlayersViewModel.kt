package com.cerridan.badmintonscheduler.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.manager.PlayerManager
import com.cerridan.badmintonscheduler.util.SingleUseEvent
import javax.inject.Inject

class PlayersViewModel @Inject constructor(
  private val playerManager: PlayerManager
) : BaseViewModel() {
  private val mutablePlayers = MutableLiveData<List<Player>>()
  val players: LiveData<List<Player>> get() = mutablePlayers

  private val mutableErrors = MutableLiveData<SingleUseEvent<String>>()
  val errors: LiveData<SingleUseEvent<String>> get() = mutableErrors

  fun onResume() {
    playerManager.getPlayers()
        .subscribe { (error, players) ->
          mutablePlayers.postValue(players)
          if (error.isNotBlank()) mutableErrors.postValue(SingleUseEvent(error))
        }
        .disposeOnClear()
  }
}
