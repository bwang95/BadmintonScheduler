package com.cerridan.badmintonscheduler.dialog

import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

abstract class BaseAlertDialogFragment : DialogFragment() {
  protected val dialogScope = CoroutineScope(Dispatchers.Main)

  protected val positiveButton: Button by lazy { (dialog as AlertDialog).getButton(BUTTON_POSITIVE) }

  protected val neutralButton: Button by lazy { (dialog as AlertDialog).getButton(BUTTON_NEUTRAL) }

  protected val negativeButton: Button by lazy { (dialog as AlertDialog).getButton(BUTTON_NEGATIVE) }

  protected val positiveButtonClicks by lazy {
    callbackFlow {
      positiveButton.setOnClickListener { dialogScope.launch { send(Unit) } }
      awaitClose { positiveButton.setOnClickListener(null) }
    }
  }

  protected val neutralButtonClicks by lazy {
    callbackFlow {
      neutralButton.setOnClickListener { dialogScope.launch { send(Unit) } }
      awaitClose { neutralButton.setOnClickListener(null) }
    }
  }

  protected val negativeButtonClicks by lazy {
    callbackFlow {
      negativeButton.setOnClickListener { dialogScope.launch { send(Unit) } }
      awaitClose { negativeButton.setOnClickListener(null) }
    }
  }

  abstract override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog

  abstract fun onStart(dialog: AlertDialog)

  open fun onStop(dialog: AlertDialog) = Unit

  final override fun onStart() {
    super.onStart()
    onStart(dialog as AlertDialog)
  }

  final override fun onStop() {
    onStop(dialog as AlertDialog)
    dialogScope.cancel()
    super.onStop()
  }
}