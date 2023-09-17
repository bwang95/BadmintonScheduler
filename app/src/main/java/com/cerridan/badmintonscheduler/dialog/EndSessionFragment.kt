package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.manager.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class EndSessionFragment : BaseAlertDialogFragment() {
  @Inject lateinit var sessionManager: SessionManager

  init {
    DaggerInjector.appComponent.inject(this)
  }

  override fun onCreateDialog(
    savedInstanceState: Bundle?
  ): AlertDialog = AlertDialog.Builder(requireContext())
    .setTitle(R.string.end_session_title)
    .setMessage(R.string.end_session_message)
    .setPositiveButton(R.string.end_session_end, null)
    .setNegativeButton(R.string.end_session_cancel, null)
    .create()

  override fun onStart(dialog: AlertDialog) {
    dialogScope.launch {
      positiveButtonClicks
        .filter { isCancelable }
        .map {
          isCancelable = false
          sessionManager.endSession()
        }
        .flowOn(Dispatchers.IO)
        .collect { error ->
          isCancelable = true
          if (error.isBlank()) {
            dismiss()
          } else {
            Toast.makeText(requireContext(), error, LENGTH_LONG).show()
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