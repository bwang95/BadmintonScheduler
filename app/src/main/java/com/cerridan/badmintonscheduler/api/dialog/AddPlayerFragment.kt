package com.cerridan.badmintonscheduler.api.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.requestFocusAndShowKeyboard
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import javax.inject.Inject

class AddPlayerFragment : DialogFragment() {
  private val nameView: TextInputEditText by bindView(R.id.et_add_player_name)
  private val passwordView: TextInputEditText by bindView(R.id.et_add_player_password)

  @Inject lateinit var service: BadmintonService

  private val disposables = CompositeDisposable()

  init { DaggerInjector.appComponent.inject(this) }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(context!!)
      .setTitle(R.string.add_player_title)
      .setView(LayoutInflater.from(context).inflate(R.layout.dialog_add_player, null))
      .setPositiveButton(R.string.add_player_add, null)
      .setNegativeButton(R.string.add_player_cancel, null)
      .create()

  override fun onResume() {
    super.onResume()

    val acceptablePasswords = resources.getStringArray(R.array.zodiac_animals)

    nameView.requestFocusAndShowKeyboard()

    Disposables.fromAction {
      (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
          ?.hideSoftInputFromWindow(nameView.windowToken, 0)
    }
    .let(disposables::add)

    (dialog as AlertDialog).getButton(BUTTON_POSITIVE)
        .clicks()
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
        .subscribe { response -> response.error?.also(nameView::setError) ?: dismiss() }
        .let(disposables::add)

    (dialog as AlertDialog).getButton(BUTTON_NEGATIVE)
        .clicks()
        .filter { isCancelable }
        .subscribe { dismiss() }
        .let(disposables::add)
  }

  override fun onPause() {
    disposables.clear()
    super.onPause()
  }
}