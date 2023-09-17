
package com.cerridan.badmintonscheduler.util

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.cerridan.badmintonscheduler.MainActivity
import com.cerridan.badmintonscheduler.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

internal const val KEY_BACKSTACK = "dialog/backstack_key"

private val Fragment.backstackId get() = arguments?.getInt(KEY_BACKSTACK, -1) ?: -1

fun Fragment.push(fragment: Fragment) = (activity as? MainActivity)
    ?.supportFragmentManager
    ?.beginTransaction()
    ?.addToBackStack(null)
    ?.add(R.id.fl_main_fragment_container, fragment)
    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    ?.commit()
    ?.let { arguments = (arguments ?: Bundle()).apply { putInt(KEY_BACKSTACK, it) } }
    ?: Unit

fun Fragment.showDialog(dialog: DialogFragment) =
  (activity as? MainActivity)?.showDialog(dialog, this)

val Fragment.backstackForegroundState: Flow<Boolean>
  get() = callbackFlow {
    val fragmentManager = (activity as? MainActivity)
      ?.supportFragmentManager
      ?: return@callbackFlow

    val listener = OnBackStackChangedListener {
      val entriesSize = fragmentManager.backStackEntryCount
      lifecycleScope.launch {
        send(entriesSize == 0 || fragmentManager.getBackStackEntryAt(entriesSize - 1).id == backstackId)
      }
    }

    fragmentManager.addOnBackStackChangedListener(listener)

    awaitClose {
      fragmentManager.removeOnBackStackChangedListener(listener)
    }
  }