package com.cerridan.badmintonscheduler.viewmodel

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

open class BaseViewModel : ViewModel() {
  private val disposables = CompositeDisposable()

  @CallSuper
  override fun onCleared() {
    disposables.clear()
    super.onCleared()
  }

  protected fun Disposable.disposeOnClear() = disposables.add(this)
}