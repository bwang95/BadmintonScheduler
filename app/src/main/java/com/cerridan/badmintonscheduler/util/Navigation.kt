package com.cerridan.badmintonscheduler.util

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import com.cerridan.badmintonscheduler.MainActivity
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.fragment.BaseFragment

fun Fragment.push(fragment: BaseFragment) {
  (activity as? MainActivity)?.supportFragmentManager
      ?.beginTransaction()
      ?.addToBackStack(null)
      ?.add(R.id.fl_main_fragment_container, fragment)
      ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
      ?.commit()
}

fun Fragment.showDialog(dialog: DialogFragment) {
  (activity as? MainActivity)?.supportFragmentManager?.let { dialog.show(it, null) }
}

fun Fragment.replace(fragment: BaseFragment) {
  (activity as? MainActivity)?.supportFragmentManager
      ?.beginTransaction()
      ?.replace(R.id.fl_main_fragment_container, fragment)
      ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
      ?.commit()
}