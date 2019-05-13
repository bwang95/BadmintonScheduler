package com.cerridan.badmintonscheduler.util

import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.view.View
import android.view.inputmethod.InputMethod.SHOW_FORCED
import android.view.inputmethod.InputMethodManager
import android.widget.ViewAnimator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread


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
    emitter.setCancellable { removeOnScrollListener(listener) }
  }
  .observeOn(mainThread())

fun View.requestFocusAndShowKeyboard() {
  requestFocus()
  (context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)
      ?.toggleSoftInput(SHOW_FORCED, 0)
}