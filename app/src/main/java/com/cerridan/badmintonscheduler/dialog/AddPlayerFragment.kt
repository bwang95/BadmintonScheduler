package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Spinner
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.PasswordsAdapter
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.combineLatest
import com.cerridan.badmintonscheduler.util.hideKeyboard
import com.cerridan.badmintonscheduler.util.requestFocusAndShowKeyboard
import com.jakewharton.rxbinding4.widget.itemSelections
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class AddPlayerFragment : BaseAlertDialogFragment() {
  private val nameView: TextInputEditText by bindView(R.id.et_add_player_name)
  private val passwordSpinner: Spinner by bindView(R.id.s_add_player_password)

  @Inject lateinit var service: BadmintonService

  private val progressSubject = BehaviorSubject.createDefault(false)

  init { DaggerInjector.appComponent.inject(this) }

  override fun onCreateDialog(
    savedInstanceState: Bundle?
  ): AlertDialog = AlertDialog.Builder(requireContext())
      .setTitle(R.string.add_player_title)
      .setView(LayoutInflater.from(context).inflate(R.layout.dialog_add_player, null))
      .setPositiveButton(R.string.add_player_add, null)
      .setNegativeButton(R.string.add_player_cancel, null)
      .create()

  override fun onResume(dialog: AlertDialog) {
    val acceptablePasswords = resources.getStringArray(R.array.zodiac_animals)
    val adapter = PasswordsAdapter(dialog.context)

    passwordSpinner.adapter = adapter

    adapter.setData(R.string.add_player_hint_password, acceptablePasswords.asList())

    nameView.requestFocusAndShowKeyboard()

    Disposable.fromAction { nameView.hideKeyboard() }
        .disposeOnPause()

    combineLatest(progressSubject, nameView.textChanges(), passwordSpinner.itemSelections())
        .map { (progress, text, selection) -> !progress && text.isNotBlank() && !adapter.isPlaceholder(selection) }
        .subscribe(positiveButton::setEnabled)
        .disposeOnPause()

    progressSubject
        .subscribe { inProgress ->
          isCancelable = !inProgress
          negativeButton.isEnabled = !inProgress
        }
        .disposeOnPause()

    positiveButtonClicks
        .filter { isCancelable }
        .map { nameView.text.toString().toLowerCase() to passwordSpinner.selectedItemPosition }
        .switchMapSingle { (name, position) ->
          service.addPlayer(Player(name, adapter.getItem(position)))
              .doOnSubscribe { progressSubject.onNext(true) }
        }
        .subscribe { response ->
          response.error
              ?.also {
                nameView.error = it
                progressSubject.onNext(false)
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