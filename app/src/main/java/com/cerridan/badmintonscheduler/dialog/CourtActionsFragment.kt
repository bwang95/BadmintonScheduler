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

class CourtActionsFragment : BaseAlertDialogFragment() {
  companion object {
    private const val KEY_COURT_NUMBER = "dialog.CourtActionsFragment/court_number"
    private const val KEY_RESERVATION_TOKEN = "dialog.CourtActionsFragment/reservation_token"

    fun create(courtNumber: Int, reservationToken: String) = CourtActionsFragment()
        .apply {
          arguments = Bundle().apply {
            putInt(KEY_COURT_NUMBER, courtNumber)
            putString(KEY_RESERVATION_TOKEN, reservationToken)
          }
        }
  }

  @Inject lateinit var service: BadmintonService

  init { DaggerInjector.appComponent.inject(this) }

  override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(context!!)
      .setTitle(
          Phrase.from(context, R.string.court_actions_title)
              .put("court", arguments?.getInt(KEY_COURT_NUMBER)?.toString() ?: "")
              .format()
      )
      .setMessage(R.string.court_actions_message)
      .setPositiveButton(R.string.court_actions_remove, null)
      .setNeutralButton(R.string.court_actions_cancel, null)
      .setNegativeButton(R.string.court_actions_reset, null)
      .create()

  override fun onResume(dialog: AlertDialog) {
    val courtNumber = arguments!!.getInt(KEY_COURT_NUMBER)
    val reservationToken = arguments!!.getString(KEY_RESERVATION_TOKEN) ?: ""

    positiveButtonClicks
        .filter { isCancelable }
        .switchMapSingle {
          service.unregisterCourt(reservationToken)
              .doOnSubscribe { isCancelable = false }
        }
        .subscribe { response ->
          response.error
              ?.also {
                Toast.makeText(dialog.context, it, LENGTH_LONG).show()
                isCancelable = true
              }
              ?: dismiss()
        }
        .disposeOnPause()

    negativeButtonClicks
        .filter { isCancelable }
        .switchMapSingle {
          service.resetCourt(courtNumber)
              .doOnSubscribe { isCancelable = false }
        }
        .subscribe { response ->
          response.error
              ?.also {
                Toast.makeText(dialog.context, it, LENGTH_LONG).show()
                isCancelable = true
              }
              ?: dismiss()
        }
        .disposeOnPause()

    neutralButtonClicks
        .filter { isCancelable }
        .subscribe { dismiss() }
        .disposeOnPause()
  }
}