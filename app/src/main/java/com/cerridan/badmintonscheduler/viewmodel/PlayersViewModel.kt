package com.cerridan.badmintonscheduler.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.manager.PlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayersViewModel @Inject constructor(
  private val playerManager: PlayerManager
) : ViewModel() {
  private val mutableErrors = MutableSharedFlow<String>()
  val errors: SharedFlow<String> get() = mutableErrors

  var players by mutableStateOf<List<Player>?>(null)
    private set
  var isLoading by mutableStateOf(false)
    private set

  fun refresh(forceUpdate: Boolean = false) {
    viewModelScope.launch(Dispatchers.IO) {
      isLoading = true
      val (error, players) = playerManager.getPlayers(forceUpdate)
      this@PlayersViewModel.players = players
      if (error.isNotBlank()) mutableErrors.emit(error)
      isLoading = false
    }
  }
}
