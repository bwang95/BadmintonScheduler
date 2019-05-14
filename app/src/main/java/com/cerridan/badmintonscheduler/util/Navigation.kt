package com.cerridan.badmintonscheduler.util

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.OnBackStackChangedListener
import com.cerridan.badmintonscheduler.MainActivity
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.fragment.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.MainThreadDisposable

fun Fragment.push(fragment: BaseFragment) {
  (activity as? MainActivity)
      ?.supportFragmentManager
      ?.beginTransaction()
      ?.addToBackStack(null)
      ?.add(R.id.fl_main_fragment_container, fragment)
      ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
      ?.commit()
}

fun Fragment.showDialog(dialog: DialogFragment) {
  (activity as? MainActivity)
      ?.supportFragmentManager
      ?.let { dialog.show(it.beginTransaction().addToBackStack(null), null) }
}

fun Fragment.replace(fragment: BaseFragment) {
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
      emitter.onNext(fragmentManager.backStackEntryCount == 0 || fragmentManager.getBackStackEntryAt(0) == this)
    }
    fragmentManager.addOnBackStackChangedListener(listener)
    emitter.setDisposable(object : MainThreadDisposable() {
      override fun onDispose() = fragmentManager.removeOnBackStackChangedListener(listener)
    })
  }