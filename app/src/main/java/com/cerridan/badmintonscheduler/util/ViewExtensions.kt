package com.cerridan.badmintonscheduler.util

import android.content.Context.INPUT_METHOD_SERVICE
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.ViewAnimator
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.rxjava3.core.Observable


var ViewAnimator.displayedChildId
  get() = currentView.id
  set(id) {
    for (k in 0 until childCount) {
      if (getChildAt(k).id == id) {
        displayedChild = k
        return
      }
    }
    throw IllegalArgumentException("Provided displayedChildId that doesn't exist")
  }

val RecyclerView.observableVerticalScrollOffset
  get() = Observable.create<Float> { emitter ->
    val listener = object : OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) =
          emitter.onNext(recyclerView.computeVerticalScrollOffset() / recyclerView.computeVerticalScrollRange().toFloat())

    }
    addOnScrollListener(listener)
    emitter.setDisposable(object : MainThreadDisposable() {
      override fun onDispose() = removeOnScrollListener(listener)
    })
  }
  .observeOn(mainThread())

fun View.requestFocusAndShowKeyboard() {
  requestFocus()
  post {
    (context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, SHOW_IMPLICIT)
  }
}

fun View.hideKeyboard() {
  (context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)
      ?.hideSoftInputFromWindow(windowToken, 0)
}