package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseFragment(@LayoutRes private val layout: Int): Fragment() {
  private val disposables = CompositeDisposable()

  final override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View = inflater.inflate(layout, container, false)

  final override fun onResume() {
    super.onResume()
    onResume(view!!)
  }

  final override fun onPause() {
    onPause(view!!)
    disposables.clear()
    super.onPause()
  }

  protected open fun onResume(view: View) = Unit

  protected open fun onPause(view: View) = Unit

  protected fun Disposable.disposeOnPause() { if (isResumed) disposables.add(this) else dispose() }
}