package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.ViewAnimator
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.CourtsAdapter
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.dialog.CourtActionsFragment
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.displayedChildId
import com.cerridan.badmintonscheduler.util.observableForegroundBackstackState
import com.cerridan.badmintonscheduler.util.push
import com.cerridan.badmintonscheduler.util.showDialog
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Inject

class CourtsFragment : BaseFragment(R.layout.fragment_courts) {
  private val animator: ViewAnimator by bindView(R.id.va_courts_animator)
  private val courtsRecycler: RecyclerView by bindView(R.id.rv_courts_recycler)
  private val registerButton: FloatingActionButton by bindView(R.id.fab_courts_register)

  @Inject lateinit var service: BadmintonService

  private lateinit var adapter: CourtsAdapter

  init { DaggerInjector.appComponent.inject(this) }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val reservationDurationMillis = TimeUnit.MINUTES.toMillis(
        view.resources.getInteger(R.integer.court_reservation_minutes).toLong()
    )
    adapter = CourtsAdapter(view.context, reservationDurationMillis)
    courtsRecycler.layoutManager = LinearLayoutManager(view.context)
    courtsRecycler.adapter = adapter
    courtsRecycler.itemAnimator = object : DefaultItemAnimator() {
      override fun canReuseUpdatedViewHolder(
          viewHolder: ViewHolder,
          payloads: MutableList<Any>
      ): Boolean = true
    }
  }

  override fun onResume(view: View) {
    super.onResume(view)

    observableForegroundBackstackState
        .startWith(true)
        .switchMap { inForeground ->
          if (inForeground) {
            Observable.interval(0, 2, MINUTES, mainThread())
                .doOnSubscribe { animator.displayedChildId = R.id.pb_courts_progress }
          } else {
            Observable.empty()
          }
        }
        .switchMapSingle { service.getCourts() }
        .doOnNext {
          animator.displayedChildId = if (it.courts.isNullOrEmpty()) {
            R.id.ll_courts_empty
          } else {
            R.id.rv_courts_recycler
          }
        }
        .subscribe { response ->
          response.error?.also { Toast.makeText(view.context, it, LENGTH_LONG).show() }
          response.courts?.let(adapter::setReservations)
        }
        .disposeOnPause()

    registerButton.clicks()
        .subscribe { push(ReservationFragment()) }
        .disposeOnPause()

    adapter.courtClicks
        .subscribe { (number, reservationToken) ->
          showDialog(CourtActionsFragment.create(number, reservationToken))
        }
        .disposeOnPause()
  }
}