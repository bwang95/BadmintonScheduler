
package com.cerridan.badmintonscheduler.util

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.OnBackStackChangedListener
import com.cerridan.badmintonscheduler.MainActivity
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.MainThreadDisposable
import java.util.UUID

const val KEY_BACKSTACK = "dialog/backstack_key"

private val Fragment.backstackKey get() = arguments?.getString(KEY_BACKSTACK) ?: ""

private fun Fragment.insertBackstackKey() {
  arguments = (arguments ?: Bundle())
      .apply { putString(KEY_BACKSTACK, UUID.randomUUID().toString()) }
}

fun Fragment.push(fragment: BaseFragment) {
  fragment.insertBackstackKey()

  (activity as? MainActivity)
      ?.supportFragmentManager
      ?.beginTransaction()
      ?.addToBackStack(fragment.backstackKey)
      ?.add(R.id.fl_main_fragment_container, fragment)
      ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
      ?.commit()
}

fun Fragment.showDialog(dialog: DialogFragment) {
  dialog.insertBackstackKey()

  (activity as? MainActivity)
      ?.supportFragmentManager
      ?.let { dialog.show(it.beginTransaction().addToBackStack(dialog.backstackKey), null) }
}

fun Fragment.replace(fragment: BaseFragment) {
  fragment.insertBackstackKey()

  (activity as? MainActivity)?.supportFragmentManager
      ?.beginTransaction()
      ?.replace(R.id.fl_main_fragment_container, fragment)
      ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
      ?.commit()
}

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
      emitter.onNext(entriesSize == 0 || fragmentManager.getBackStackEntryAt(entriesSize - 1).name == backstackKey)
    }
    fragmentManager.addOnBackStackChangedListener(listener)
    emitter.setDisposable(object : MainThreadDisposable() {
      override fun onDispose() = fragmentManager.removeOnBackStackChangedListener(listener)
    })
  }