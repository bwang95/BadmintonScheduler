package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.manager.PlayerManager
import com.cerridan.badmintonscheduler.util.GlobalPadding
import com.cerridan.badmintonscheduler.util.combineLatest
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class AddPlayerFragment : BaseAlertDialogFragment() {
  @Inject lateinit var playerManager: PlayerManager

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
      .setView(ComposeView(requireContext()).apply { setContent { AddPlayerContent() } })
      .setPositiveButton(R.string.add_player_add, null)
      .setNegativeButton(R.string.add_player_cancel, null)
      .create()

  @Composable fun AddPlayerContent() {
    var isPasswordExpanded by remember { mutableStateOf(false) }
    val acceptablePasswords = stringArrayResource(R.array.zodiac_animals)

    LaunchedEffect(Unit) { password.value = acceptablePasswords.first() }

    Column(
        modifier = Modifier
            .padding(GlobalPadding)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(GlobalPadding)
    ) {
      TextField(
          modifier = Modifier.fillMaxWidth(),
          value = name.value,
          label = { Text(stringResource(R.string.add_player_hint_name)) },
          isError = error.value.isNotBlank(),
          onValueChange = { name.value = it }
      )
      Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isPasswordExpanded = !isPasswordExpanded }
                .padding(GlobalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(password.value)
          Icon(painterResource(R.drawable.icon_drop_down), contentDescription = null)
        }

        DropdownMenu(
            expanded = isPasswordExpanded,
            onDismissRequest = { isPasswordExpanded = false }
        ) {
          acceptablePasswords.forEach {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      password.value = it
                      isPasswordExpanded = false
                    }
                    .padding(GlobalPadding),
                text = it
            )
          }
        }
      }
    }

    LaunchedEffect(error.value) {
      if (error.value.isNotBlank()) Toast.makeText(dialog?.context, error.value, LENGTH_SHORT).show()
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
          playerManager.addPlayer(Player(name.value, password.value))
              .doOnSubscribe { progressSubject.onNext(true) }
        }
        .subscribe {
          error.value = it
          progressSubject.onNext(false)
          if (it.isBlank()) dismiss()
        }
        .disposeOnPause()

    negativeButtonClicks
        .filter { isCancelable }
        .subscribe { dismiss() }
        .disposeOnPause()
  }
}