package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.PasswordsAdapter
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.manager.PlayerManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddPlayerFragment : BaseAlertDialogFragment() {
  @Inject lateinit var playerManager: PlayerManager

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

  override fun onStart(dialog: AlertDialog) {
    val nameView: TextInputEditText = dialog.findViewById(R.id.et_add_player_name) ?: return
    val passwordSpinner: Spinner = dialog.findViewById(R.id.s_add_player_password) ?: return

    val acceptablePasswords = resources.getStringArray(R.array.zodiac_animals)
    val adapter = PasswordsAdapter(dialog.context)

    val inProgress = MutableStateFlow(false)
    val nameChanges = callbackFlow {
      trySend("")
      val watcher = nameView.addTextChangedListener { trySend(it?.toString() ?: "") }
      awaitClose { nameView.removeTextChangedListener(watcher) }
    }
    val passwordSelections = callbackFlow {
      trySend(-1)

      passwordSpinner.onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          trySend(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) { trySend(-1) }
      }
      awaitClose { passwordSpinner.onItemSelectedListener = null }
    }

    passwordSpinner.adapter = adapter

    adapter.setData(R.string.add_player_hint_password, acceptablePasswords.asList())

    nameView.requestFocus()
    nameView.post {
      nameView.context.getSystemService<InputMethodManager>()
          ?.showSoftInput(nameView, InputMethodManager.SHOW_IMPLICIT)
    }

    dialogScope.launch {
      try {
        awaitCancellation()
      } catch (e: CancellationException) {
        nameView.context.getSystemService<InputMethodManager>()
          ?.hideSoftInputFromWindow(nameView.windowToken, 0)
      }
    }

    dialogScope.launch {
      combine(nameChanges, passwordSelections, inProgress) { name, password, progress ->
        !progress && name.isNotBlank() && password != -1 && !adapter.isPlaceholder(password)
      }
        .collect(positiveButton::setEnabled)
    }

    dialogScope.launch {
      inProgress.collect {
        isCancelable = !it
        negativeButton.isEnabled = !it
      }
    }

    dialogScope.launch {
      positiveButtonClicks
        .filter { isCancelable }
        .map { nameView.text.toString().lowercase() to passwordSpinner.selectedItemPosition }
        .map { (name, position) ->
          inProgress.value = true
          playerManager.addPlayer(Player(name, adapter.getItem(position)))
        }
        .flowOn(Dispatchers.IO)
        .collect {
          inProgress.value = false
          if (it.isBlank()) {
            dismiss()
          } else {
            nameView.error = it
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