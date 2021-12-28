package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.squareup.phrase.Phrase
import javax.inject.Inject

class RemovePlayerFragment : BaseAlertDialogFragment() {
  companion object {
    private const val KEY_PLAYER_NAME = "dialog.RemovePlayerFragment/player_name"

    fun create(playerName: String) = RemovePlayerFragment()
        .apply { arguments = Bundle().apply { putString(KEY_PLAYER_NAME, playerName) } }
  }

  @Inject lateinit var service: BadmintonService

  init { DaggerInjector.appComponent.inject(this) }

  override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
      .setTitle(R.string.remove_player_title)
      .setMessage(
          Phrase.from(context, R.string.remove_player_message)
              .put("player", arguments?.getString(KEY_PLAYER_NAME)!!)
              .format()
      )
      .setPositiveButton(R.string.remove_player_confirm, null)
      .setNegativeButton(R.string.remove_player_cancel, null)
      .create()

  override fun onResume(dialog: AlertDialog) {
    val playerName = arguments?.getString(KEY_PLAYER_NAME)!!

    positiveButtonClicks
        .filter { isCancelable }
        .switchMapSingle {
          service.removePlayer(playerName)
              .doOnSubscribe { isCancelable = false }
        }
        .subscribe { response ->
          response.error
              ?.also {
                isCancelable = true
                Toast.makeText(dialog.context, it, LENGTH_LONG).show()
              }
              ?: dismiss()
        }
        .disposeOnPause()

    negativeButtonClicks
        .filter { isCancelable }
        .subscribe { dismiss() }
        .disposeOnPause()
  }
}