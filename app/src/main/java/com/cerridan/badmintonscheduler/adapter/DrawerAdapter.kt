package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import androidx.annotation.LayoutRes
import android.view.View
import com.cerridan.badmintonscheduler.BuildConfig
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.DrawerAdapter.Row.NavigationRow
import com.cerridan.badmintonscheduler.adapter.DrawerAdapter.Row.VersionRow
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor
import com.cerridan.badmintonscheduler.view.DrawerItemView
import com.cerridan.badmintonscheduler.view.VersionItemView
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class DrawerAdapter(context: Context) : BaseRecyclerViewAdapter(context) {
  sealed class Row(@LayoutRes val layout: Int) {
    class VersionRow(val version: String) : Row(R.layout.item_version)

    class NavigationRow(val descriptor: DrawerNavigableFragmentDescriptor) : Row(R.layout.item_drawer)
  }

  private val fragmentClicksSubject = PublishSubject.create<DrawerNavigableFragmentDescriptor>()
  private val rows = listOf(VersionRow(BuildConfig.VERSION_NAME)) +
                     DrawerNavigableFragmentDescriptor.values().map(::NavigationRow)

  val fragmentDescriptorClicks: Observable<DrawerNavigableFragmentDescriptor> get() = fragmentClicksSubject

  override fun getLayoutForViewType(viewType: Int) = viewType

  override fun onViewAttachedToWindow(holder: ViewHolder, view: View, position: Int): Unit =
      when (val row = rows[position]) {
        is VersionRow -> (view as VersionItemView).bind(row.version)
        is NavigationRow -> (view as DrawerItemView).run {
          bind(row.descriptor)
          clicks()
              .map { row.descriptor }
              .subscribe(fragmentClicksSubject::onNext)
              .disposeOnRecycle(holder)
        }
      }

  override fun getItemViewType(position: Int) = rows[position].layout

  override fun getItemCount() = rows.size
}