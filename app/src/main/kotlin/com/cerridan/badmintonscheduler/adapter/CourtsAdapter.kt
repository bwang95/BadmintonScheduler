package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.CourtsAdapter.Row.CourtRow
import com.cerridan.badmintonscheduler.adapter.CourtsAdapter.Row.ReservationRow
import com.cerridan.badmintonscheduler.api.model.Reservation
import com.cerridan.badmintonscheduler.view.CourtItemView
import com.cerridan.badmintonscheduler.view.ReservationItemView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.Date

class CourtsAdapter(
    context: Context,
    private val reservationDurationMillis: Long
) : BaseRecyclerViewAdapter(context) {
  sealed class Row(@LayoutRes val layout: Int) {
    class CourtRow(
        val number: Int,
        val firstReservationToken: String,
        val expiry: Date
    ) : Row(R.layout.item_court)

    class ReservationRow(val reservation: Reservation) : Row(R.layout.item_reservation)
  }

  private val rows = mutableListOf<Row>()
  private val courtClicksSubject = PublishSubject.create<Pair<Int, String>>()

  val courtClicks: Observable<Pair<Int, String>> get() = courtClicksSubject

  init { setHasStableIds(true) }

  fun setReservations(reservations: List<Reservation>) {
    rows.clear()
    reservations
        .sortedBy(Reservation::startsAt)
        .groupBy(Reservation::courtNumber)
        .toSortedMap()
        .forEach { (number, reservations) ->
          val expiry = Date(reservations.last().startsAt.time + reservationDurationMillis)
          rows += CourtRow(number, reservations.first().token, expiry)
          rows += reservations.map(::ReservationRow)
        }
    notifyDataSetChanged()
  }

  override fun getLayoutForViewType(viewType: Int) = viewType

  override fun onViewAttachedToWindow(holder: ViewHolder, view: View, position: Int) {
    when (val row = rows[position]) {
      is CourtRow -> (view as CourtItemView).apply {
        bind(row.number, row.expiry)
        clicks()
            .map { row.number to row.firstReservationToken }
            .subscribe(courtClicksSubject::onNext)
            .disposeOnRecycle(holder)
      }
      is ReservationRow -> (view as ReservationItemView).bind(row.reservation, reservationDurationMillis)
    }
  }

  override fun getItemId(position: Int) = when (val row = rows[position]) {
    is CourtRow -> row.number.toLong() + row.expiry.time
    is ReservationRow -> row.reservation.let { it.token.hashCode().toLong() + it.startsAt.time }
  }

  override fun getItemViewType(position: Int) = rows[position].layout

  override fun getItemCount() = rows.size
}