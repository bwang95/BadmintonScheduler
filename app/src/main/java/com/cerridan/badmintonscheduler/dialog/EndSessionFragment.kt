package com.cerridan.badmintonscheduler.dialog

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.manager.SessionManager
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class EndSessionFragment : BaseAlertDialogFragment() {
  @Inject lateinit var sessionManager: SessionManager

  private val progressSubject = BehaviorSubject.createDefault(false)

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

  override fun onResume(dialog: AlertDialog) {
    positiveButtonClicks
      .filter { isCancelable }
      .switchMapSingle {
        sessionManager.endSession()
          .doOnSubscribe { progressSubject.onNext(true) }
      }
      .subscribe {
        progressSubject.onNext(false)
        if (it.isBlank()) {
          dismiss()
        } else {
          Toast.makeText(requireContext(), it, LENGTH_LONG).show()
        }
      }
      .disposeOnPause()

    negativeButtonClicks
      .filter { isCancelable }
      .subscribe { dismiss() }
      .disposeOnPause()
  }
}