package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor
import com.cerridan.badmintonscheduler.view.DrawerItemView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DrawerAdapter(context: Context) : BaseRecyclerViewAdapter(context) {
  private val rows = DrawerNavigableFragmentDescriptor.values()
  private val fragmentClicksSubject = PublishSubject.create<DrawerNavigableFragmentDescriptor>()

  val fragmentDescriptorClicks: Observable<DrawerNavigableFragmentDescriptor> get() = fragmentClicksSubject

  override fun getLayoutForViewType(viewType: Int) = R.layout.item_drawer

  override fun onViewAttachedToWindow(holder: ViewHolder, view: View, position: Int) {
    (view as DrawerItemView).apply {
      val row = rows[position]
      bind(row)
      clicks()
          .map { row }
          .subscribe(fragmentClicksSubject::onNext)
          .disposeOnRecycle(holder)
    }
  }

  override fun getItemCount() = rows.size
}