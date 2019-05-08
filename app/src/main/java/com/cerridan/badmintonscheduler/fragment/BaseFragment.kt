package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment(@LayoutRes private val layout: Int): Fragment() {
  private val disposables = CompositeDisposable()

  final override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View = inflater.inflate(layout, container, false)

  final override fun onStart() {
    super.onStart()
    onStart(checkNotNull(view))
  }

  final override fun onStop() {
    onStop(checkNotNull(view))
    disposables.clear()
    super.onStop()
  }

  protected open fun onStart(view: View) = Unit

  protected open fun onStop(view: View) = Unit

  protected fun Disposable.disposeOnStop() = disposables.add(this)
}