package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseRecyclerViewAdapter(
    context: Context
) : RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder>() {
  class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val disposables = CompositeDisposable()

    internal fun dispose(): Unit = disposables.clear()

    internal fun disposeOnRecycle(disposable: Disposable) { disposables.add(disposable) }
  }

  private val inflater = LayoutInflater.from(context)

  @CallSuper
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    
    (recyclerView.layoutManager!! as? LinearLayoutManager)?.recycleChildrenOnDetach = true
  }

  final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      ViewHolder(inflater.inflate(getLayoutForViewType(viewType), parent, false))

  final override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) = Unit

  override fun onViewRecycled(holder: ViewHolder) {
    holder.dispose()
    super.onViewRecycled(holder)
  }

  final override fun onViewAttachedToWindow(holder: ViewHolder) =
      onViewAttachedToWindow(holder, holder.view, holder.adapterPosition)

  @LayoutRes abstract fun getLayoutForViewType(viewType: Int): Int

  abstract fun onViewAttachedToWindow(holder: ViewHolder, view: View, position: Int)

  protected fun Disposable.disposeOnRecycle(viewholder: ViewHolder): Unit =
      if (!viewholder.view.isAttachedToWindow) dispose()
      else viewholder.disposeOnRecycle(this)
}