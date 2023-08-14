package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.manager.PlayerManager
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

  override fun onResume(dialog: AlertDialog) {
    val playerName = arguments?.getString(KEY_PLAYER_NAME)!!

    positiveButtonClicks
        .filter { isCancelable }
        .switchMapSingle {
          playerManager.removePlayer(playerName)
              .doOnSubscribe { isCancelable = false }
        }
        .subscribe {
          isCancelable = true
          if (it.isNotBlank()) {
            Toast.makeText(dialog.context, it, LENGTH_LONG).show()
          } else {
            dismiss()
          }
        }
        .disposeOnPause()

    negativeButtonClicks
        .filter { isCancelable }
        .subscribe { dismiss() }
        .disposeOnPause()
  }
}