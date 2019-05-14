package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.hideKeyboard
import com.cerridan.badmintonscheduler.util.requestFocusAndShowKeyboard
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import javax.inject.Inject

class AddPlayerFragment : BaseAlertDialogFragment() {
  private val nameView: TextInputEditText by bindView(R.id.et_add_player_name)
  private val passwordView: TextInputEditText by bindView(R.id.et_add_player_password)

  @Inject lateinit var service: BadmintonService

  init { DaggerInjector.appComponent.inject(this) }

  override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = AlertDialog.Builder(context!!)
      .setTitle(R.string.add_player_title)
      .setView(LayoutInflater.from(context).inflate(R.layout.dialog_add_player, null))
      .setPositiveButton(R.string.add_player_add, null)
      .setNegativeButton(R.string.add_player_cancel, null)
      .create()

  override fun onResume(dialog: AlertDialog) {
    val acceptablePasswords = resources.getStringArray(R.array.zodiac_animals)

    nameView.requestFocusAndShowKeyboard()

    Disposables.fromAction { nameView.hideKeyboard() }
        .disposeOnPause()

    positiveButtonClicks
        .filter { isCancelable }
        .map { nameView.text.toString().toLowerCase() to passwordView.text.toString().toLowerCase() }
        .switchMapSingle { (name, password) ->
          when {
            name.isBlank() -> {
              nameView.error = resources.getString(R.string.add_player_name_error)
              Single.never()
            }
            !acceptablePasswords.contains(password) -> {
              passwordView.error = resources.getString(R.string.add_player_password_error)
              Single.never()
            }
            else -> service.addPlayer(Player(name, password))
                .doOnSubscribe { isCancelable = false }
          }
        }
        .subscribe { response ->
          response.error
              ?.also {
                nameView.error = it
                isCancelable = true
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