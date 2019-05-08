package com.cerridan.badmintonscheduler.util

import android.widget.ViewAnimator

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