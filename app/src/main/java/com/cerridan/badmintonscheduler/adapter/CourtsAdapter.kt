package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.CourtsAdapter.Row.CourtRow
import com.cerridan.badmintonscheduler.adapter.CourtsAdapter.Row.RegistrationRow
import com.cerridan.badmintonscheduler.api.model.Court
import com.cerridan.badmintonscheduler.api.model.Court.Registration
import com.cerridan.badmintonscheduler.view.CourtItemView
import com.cerridan.badmintonscheduler.view.RegistrationItemView
import java.util.Date

class CourtsAdapter(
    context: Context,
    private val registrationDurationMillis: Long
) : BaseRecyclerViewAdapter(context) {
  sealed class Row(@LayoutRes val layout: Int) {
    class CourtRow(val number: Int, val expiry: Date) : Row(R.layout.item_court)

    class RegistrationRow(val registration: Registration) : Row(R.layout.item_registration)
  }

  private val rows = mutableListOf<Row>()

  init { setHasStableIds(true) }

  fun setCourts(courts: List<Court>) {
    rows.clear()
    courts.forEach { (number, registrations) ->
      val expiry = registrations.maxBy(Registration::startTime)
          ?.startTime
          ?.time
          ?.let { Date(it + registrationDurationMillis) }
          ?: Date()

      rows += CourtRow(number, expiry)
      rows += registrations.map(::RegistrationRow)
    }
    notifyDataSetChanged()
  }

  override fun getLayoutForViewType(viewType: Int) = viewType

  override fun onViewAttachedToWindow(holder: ViewHolder, view: View, position: Int) {
    when (val row = rows[position]) {
      is CourtRow -> (view as CourtItemView).bind(row.number, row.expiry)
      is RegistrationRow -> (view as RegistrationItemView).bind(row.registration, registrationDurationMillis)
    }
  }

  override fun getItemId(position: Int) = when (val row = rows[position]) {
    is CourtRow -> row.number.toLong()
    is RegistrationRow -> row.registration.players.sumBy(String::hashCode).toLong()
  }

  override fun getItemViewType(position: Int) = rows[position].layout

  override fun getItemCount() = rows.size
}