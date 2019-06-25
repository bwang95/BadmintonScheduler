
package com.cerridan.badmintonscheduler.util

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.OnBackStackChangedListener
import android.support.v4.app.FragmentTransaction
import com.cerridan.badmintonscheduler.MainActivity
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.MainThreadDisposable

private const val KEY_BACKSTACK = "dialog/backstack_key"

private val Fragment.backstackId get() = arguments?.getInt(KEY_BACKSTACK, -1) ?: -1

fun Fragment.push(fragment: BaseFragment) = (activity as? MainActivity)
    ?.supportFragmentManager
    ?.beginTransaction()
    ?.addToBackStack(null)
    ?.add(R.id.fl_main_fragment_container, fragment)
    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    ?.commit()
    ?.let { arguments = (arguments ?: Bundle()).apply { putInt(KEY_BACKSTACK, it) } }
    ?: Unit

fun Fragment.showDialog(dialog: DialogFragment) = (activity as? MainActivity)
    ?.supportFragmentManager
    ?.let { dialog.show(it.beginTransaction().addToBackStack(null), null) }
    ?.let { arguments = (arguments ?: Bundle()).apply { putInt(KEY_BACKSTACK, it) } }
    ?: Unit

fun Fragment.replace(fragment: BaseFragment) = (activity as? MainActivity)
    ?.supportFragmentManager
    ?.beginTransaction()
    ?.replace(R.id.fl_main_fragment_container, fragment)
    ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
    ?.commit()
    ?: Unit

val Fragment.observableForegroundBackstackState: Observable<Boolean>
  get() = Observable.create { emitter ->
    val fragmentManager = (activity as? MainActivity)
        ?.supportFragmentManager
        ?: run {
          emitter.onComplete()
          return@create
        }

    val listener = OnBackStackChangedListener {
      val entriesSize = fragmentManager.backStackEntryCount
      emitter.onNext(entriesSize == 0 || fragmentManager.getBackStackEntryAt(entriesSize - 1).id == backstackId)
    }
    fragmentManager.addOnBackStackChangedListener(listener)
    emitter.setDisposable(object : MainThreadDisposable() {
      override fun onDispose() = fragmentManager.removeOnBackStackChangedListener(listener)
    })
  }