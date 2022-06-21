package com.cerridan.badmintonscheduler.dialog

import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseAlertDialogFragment : DialogFragment() {
  protected val positiveButton: Button by lazy { (dialog as AlertDialog).getButton(BUTTON_POSITIVE) }

  protected val neutralButton: Button by lazy { (dialog as AlertDialog).getButton(BUTTON_NEUTRAL) }

  protected val negativeButton: Button by lazy { (dialog as AlertDialog).getButton(BUTTON_NEGATIVE) }

  protected val positiveButtonClicks get() = positiveButton.clicks()

  protected val neutralButtonClicks get() = neutralButton.clicks()

  protected val negativeButtonClicks get() = negativeButton.clicks()

  private val disposables = CompositeDisposable()

  abstract override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog

  final override fun onPause() {
    onPause(dialog as AlertDialog)
    disposables.clear()
    super.onPause()
  }

  final override fun onResume() {
    super.onResume()
    onResume(dialog as AlertDialog)
  }

  open fun onPause(dialog: AlertDialog) = Unit

  abstract fun onResume(dialog: AlertDialog)

  protected fun Disposable.disposeOnPause() { if (isResumed) disposables.add(this) else dispose() }
}