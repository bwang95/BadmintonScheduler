package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.manager.ReservationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class CourtActionsFragment : BaseAlertDialogFragment() {
  companion object {
    private const val KEY_COURT_NUMBER = "dialog.CourtActionsFragment/court_number"
    private const val KEY_RESERVATION_TOKEN = "dialog.CourtActionsFragment/reservation_token"

    fun create(courtNumber: String, reservationToken: String) = CourtActionsFragment()
        .apply {
          arguments = Bundle().apply {
            putString(KEY_COURT_NUMBER, courtNumber)
            putString(KEY_RESERVATION_TOKEN, reservationToken)
          }
        }
  }

  @Inject lateinit var manager: ReservationManager

  init { DaggerInjector.appComponent.inject(this) }

  override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
      .setTitle(
        resources.getString(
          R.string.court_actions_title,
          arguments?.getString(KEY_COURT_NUMBER) ?: ""
        )
      )
      .setMessage(R.string.court_actions_message)
      .setPositiveButton(R.string.court_actions_remove, null)
      .setNeutralButton(R.string.court_actions_cancel, null)
      .setNegativeButton(R.string.court_actions_reset, null)
      .create()

  override fun onStart(dialog: AlertDialog) {
    val courtNumber = requireArguments().getString(KEY_COURT_NUMBER) ?: ""
    val reservationToken = requireArguments().getString(KEY_RESERVATION_TOKEN) ?: ""

    dialogScope.launch {
      positiveButtonClicks
        .filter { isCancelable }
        .map {
          isCancelable = false
          manager.deleteReservation(reservationToken)
        }
        .collect { error ->
          error.takeIf(String::isNotBlank)
            ?.let {
              Toast.makeText(dialog.context, it, LENGTH_LONG).show()
              isCancelable = true
            }
            ?: dismiss()
        }
    }

    dialogScope.launch {
      negativeButtonClicks
        .filter { isCancelable }
        .map {
          isCancelable = false
          manager.resetCourt(courtNumber)
        }
        .flowOn(Dispatchers.IO)
        .collect { error ->
          error.takeIf(String::isNotBlank)
            ?.let {
              Toast.makeText(dialog.context, it, LENGTH_LONG).show()
              isCancelable = true
            }
            ?: dismiss()
        }
    }

    dialogScope.launch {
      neutralButtonClicks
        .filter { isCancelable }
        .collect { dismiss() }
    }
  }
}