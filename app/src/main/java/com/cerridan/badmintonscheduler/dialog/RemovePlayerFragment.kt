package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.manager.PlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class RemovePlayerFragment : BaseAlertDialogFragment() {
  companion object {
    private const val KEY_PLAYER_NAME = "dialog.RemovePlayerFragment/player_name"

    fun create(playerName: String) = RemovePlayerFragment()
        .apply { arguments = Bundle().apply { putString(KEY_PLAYER_NAME, playerName) } }
  }

  @Inject lateinit var playerManager: PlayerManager

  init { DaggerInjector.appComponent.inject(this) }

  override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
      .setTitle(R.string.remove_player_title)
      .setMessage(
        resources.getString(
          R.string.remove_player_message,
          arguments?.getString(KEY_PLAYER_NAME)!!
        )
      )
      .setPositiveButton(R.string.remove_player_confirm, null)
      .setNegativeButton(R.string.remove_player_cancel, null)
      .create()

  override fun onStart(dialog: AlertDialog) {
    val playerName = arguments?.getString(KEY_PLAYER_NAME)!!

    dialogScope.launch {
      positiveButtonClicks
        .filter { isCancelable }
        .map {
          isCancelable = false
          playerManager.removePlayer(playerName)
        }
        .flowOn(Dispatchers.IO)
        .collect { error ->
          isCancelable = true
          if (error.isNotBlank()) {
            Toast.makeText(dialog.context, error, LENGTH_LONG).show()
          } else {
            dismiss()
          }
        }
    }

    dialogScope.launch {
      negativeButtonClicks
        .filter { isCancelable }
        .collect { dismiss() }
    }
  }
}