package com.cerridan.badmintonscheduler.dialog

import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseAlertDialogFragment : DialogFragment() {
  protected val positiveButtonClicks
    get() = (dialog as AlertDialog).getButton(BUTTON_POSITIVE).clicks()

  protected val neutralButtonClicks
    get() = (dialog as AlertDialog).getButton(BUTTON_NEUTRAL).clicks()

  protected val negativeButtonClicks
    get() = (dialog as AlertDialog).getButton(BUTTON_NEGATIVE).clicks()

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

  protected fun Disposable.disposeOnPause() = disposables.add(this)
}