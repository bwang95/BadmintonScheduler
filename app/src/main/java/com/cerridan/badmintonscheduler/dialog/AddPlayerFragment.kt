package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import androidx.core.content.getSystemService
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.PasswordsAdapter
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.manager.PlayerManager
import com.cerridan.badmintonscheduler.util.combineLatest
import com.jakewharton.rxbinding4.widget.itemSelections
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class AddPlayerFragment : BaseAlertDialogFragment() {
  @Inject lateinit var playerManager: PlayerManager

  private val progressSubject = BehaviorSubject.createDefault(false)

  init {
    DaggerInjector.appComponent.inject(this)
  }

  override fun onCreateDialog(
    savedInstanceState: Bundle?
  ): AlertDialog = AlertDialog.Builder(requireContext())
      .setTitle(R.string.add_player_title)
      .setView(
          LayoutInflater.from(context)
              .inflate(R.layout.dialog_add_player, null)
      )
      .setPositiveButton(R.string.add_player_add, null)
      .setNegativeButton(R.string.add_player_cancel, null)
      .create()

  override fun onResume(dialog: AlertDialog) {
    val nameView: TextInputEditText = dialog.findViewById(R.id.et_add_player_name) ?: return
    val passwordSpinner: Spinner = dialog.findViewById(R.id.s_add_player_password) ?: return

    val acceptablePasswords = resources.getStringArray(R.array.zodiac_animals)
    val adapter = PasswordsAdapter(dialog.context)

    passwordSpinner.adapter = adapter

    adapter.setData(R.string.add_player_hint_password, acceptablePasswords.asList())

    nameView.requestFocus()
    nameView.post {
      nameView.context.getSystemService<InputMethodManager>()
          ?.showSoftInput(nameView, InputMethodManager.SHOW_IMPLICIT)
    }

    Disposable.fromAction {
      nameView.context.getSystemService<InputMethodManager>()
          ?.hideSoftInputFromWindow(nameView.windowToken, 0)
    }
        .disposeOnPause()

    combineLatest(progressSubject, nameView.textChanges(), passwordSpinner.itemSelections())
        .map { (progress, text, selection) ->
          !progress && text.isNotBlank() && !adapter.isPlaceholder(
              selection
          )
        }
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
          playerManager.addPlayer(Player(name, adapter.getItem(position)))
              .doOnSubscribe { progressSubject.onNext(true) }
        }
        .subscribe {
          progressSubject.onNext(false)
          if (it.isBlank()) {
            dismiss()
          } else {
            nameView.error = it
          }
        }
        .disposeOnPause()

    negativeButtonClicks
        .filter { isCancelable }
        .subscribe { dismiss() }
        .disposeOnPause()
  }
}