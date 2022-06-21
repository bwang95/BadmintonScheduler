package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringArrayResource
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.util.combineLatest
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class AddPlayerFragment : BaseAlertDialogFragment() {
  @Inject lateinit var service: BadmintonService

  private val progressSubject = BehaviorSubject.createDefault(false)
  private val isInputValid = BehaviorSubject.createDefault(false)
  private val name = mutableStateOf("")
  private val password = mutableStateOf("")
  private val error = mutableStateOf("")

  init { DaggerInjector.appComponent.inject(this) }

  override fun onCreateDialog(
    savedInstanceState: Bundle?
  ): AlertDialog = AlertDialog.Builder(requireContext())
      .setTitle(R.string.add_player_title)
      .setView(ComposeView(requireContext()))
      .setPositiveButton(R.string.add_player_add, null)
      .setNegativeButton(R.string.add_player_cancel, null)
      .create()

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) = (view as ComposeView).setContent {
    var isPasswordExpanded by remember { mutableStateOf(false) }

    TextField(
      value = name.value,
      isError = error.value.isNotBlank(),
      onValueChange = { name.value = it }
    )
    Box {
      Text(password.value)
      DropdownMenu(
        expanded = isPasswordExpanded,
        onDismissRequest = { isPasswordExpanded = false },
        content = { stringArrayResource(R.array.zodiac_animals).forEach { Text(it) } }
      )
    }

    LaunchedEffect(name.value, password.value) {
      isInputValid.onNext(name.value.isNotBlank() && password.value.isNotBlank())
    }
  }

  override fun onResume(dialog: AlertDialog) {
    combineLatest(progressSubject, isInputValid)
        .map { (progress, valid) -> !progress && valid }
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
        .switchMapSingle {
          service.addPlayer(Player(name.value, password.value))
              .doOnSubscribe { progressSubject.onNext(true) }
        }
        .subscribe { response ->
          response.error
              ?.also {
                error.value = it
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