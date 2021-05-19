package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import androidx.annotation.LayoutRes
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.CourtsAdapter.Row.CourtRow
import com.cerridan.badmintonscheduler.adapter.CourtsAdapter.Row.ReservationRow
import com.cerridan.badmintonscheduler.api.model.Reservation
import com.cerridan.badmintonscheduler.view.CourtItemView
import com.cerridan.badmintonscheduler.view.ReservationItemView
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.Date
import java.util.concurrent.TimeUnit.SECONDS

class CourtsAdapter(
    context: Context,
    private val reservationDurationMillis: Long
) : BaseRecyclerViewAdapter(context) {
  sealed class Row(@LayoutRes val layout: Int) {
    class CourtRow(
        val number: Int,
        val firstReservationToken: String,
        val start: Date,
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
          rows += CourtRow(number, reservations.first().token, reservations.first().startsAt, expiry)
          rows += reservations.map(::ReservationRow)
        }
    notifyDataSetChanged()
  }

  override fun getLayoutForViewType(viewType: Int) = viewType

  override fun onViewAttachedToWindow(holder: ViewHolder, view: View, position: Int) {
    when (val row = rows[position]) {
      is CourtRow -> (view as CourtItemView).apply {
        Observable.interval(0L, 30, SECONDS, mainThread())
            .subscribe { bind(row.number, row.start, row.expiry) }
            .disposeOnRecycle(holder)
        clicks()
            .map { row.number to row.firstReservationToken }
            .subscribe(courtClicksSubject::onNext)
            .disposeOnRecycle(holder)
      }
      is ReservationRow -> Observable.interval(0L, 30, SECONDS, mainThread())
          .subscribe { (view as ReservationItemView).bind(row.reservation, reservationDurationMillis) }
          .disposeOnRecycle(holder)
    }
  }

  override fun getItemId(position: Int) = when (val row = rows[position]) {
    is CourtRow -> row.number.toLong() + row.expiry.time
    is ReservationRow -> row.reservation.let { it.token.hashCode().toLong() + it.startsAt.time }
  }

  override fun getItemViewType(position: Int) = rows[position].layout

  override fun getItemCount() = rows.size
}