package com.cerridan.badmintonscheduler.util

class SingleUseEvent<T>(
  private var initialValue: T?
) {
  val value: T?
    get() {
      val result = initialValue
      initialValue = null
      return result
    }
}