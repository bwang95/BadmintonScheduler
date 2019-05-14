package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import java.util.concurrent.TimeUnit
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

    val registrationDurationMillis = TimeUnit.MINUTES.toMillis(
        view.resources.getInteger(R.integer.court_registration_minutes).toLong()
    )
    adapter = CourtsAdapter(view.context, registrationDurationMillis)
    courtsRecycler.layoutManager = LinearLayoutManager(view.context)
    courtsRecycler.adapter = adapter
  }

  override fun onResume(view: View) {
    super.onResume(view)

    observableForegroundBackstackState
        .filter { it }
        .startWith(true)
        .switchMapSingle {
          service.getCourts()
              .doOnSubscribe { animator.displayedChildId = R.id.pb_courts_progress }
        }
        .doOnNext {
          animator.displayedChildId = if (it.courts.isNullOrEmpty()) {
            R.id.ll_courts_empty
          } else {
            R.id.rv_courts_recycler
          }
        }
        .subscribe { response ->
          response.error?.also { Toast.makeText(view.context, it, LENGTH_LONG).show() }
          response.courts?.let(adapter::setCourts)
        }
        .disposeOnPause()

    registerButton.clicks()
        .subscribe { push(RegistrationFragment()) }
        .disposeOnPause()

    adapter.courtClicks
        .subscribe { showDialog(CourtActionsFragment.create(it)) }
        .disposeOnPause()
  }
}