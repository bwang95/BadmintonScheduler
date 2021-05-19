package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.ViewAnimator
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.SelectablePlayersAdapter
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.combineLatest
import com.cerridan.badmintonscheduler.util.displayedChildId
import com.cerridan.badmintonscheduler.util.hideKeyboard
import com.cerridan.badmintonscheduler.util.requestFocusAndShowKeyboard
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class ReservationFragment : BaseFragment(R.layout.fragment_reservation) {
  private val courtNumberView: EditText by bindView(R.id.et_reservation_court_number)
  private val delayTimeView: EditText by bindView(R.id.et_reservation_delay_time)
  private val playersAnimator: ViewAnimator by bindView(R.id.va_reservation_players_animator)
  private val playersRecycler: RecyclerView by bindView(R.id.rv_reservation_players_recycler)
  private val submitButton: Button by bindView(R.id.b_reservation_submit)

  private lateinit var adapter: SelectablePlayersAdapter

  private val progressSubject = BehaviorSubject.createDefault(false)

  @Inject lateinit var service: BadmintonService

  init { DaggerInjector.appComponent.inject(this) }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    adapter = SelectablePlayersAdapter(view.context)
    playersRecycler.addItemDecoration(DividerItemDecoration(view.context, VERTICAL))
    playersRecycler.layoutManager = LinearLayoutManager(view.context)
    playersRecycler.adapter = adapter
  }

  override fun onResume(view: View) {
    super.onResume(view)

    courtNumberView.requestFocusAndShowKeyboard()

    Disposable.fromAction { courtNumberView.hideKeyboard() }
        .disposeOnPause()

    combineLatest(progressSubject, courtNumberView.textChanges(), adapter.observablePlayerSelections.map { Unit }.startWithItem(Unit))
        .map { (progress, text, _) -> !progress && text.isNotBlank() && adapter.selectedPlayers.isNotEmpty() }
        .subscribe(submitButton::setEnabled)
        .disposeOnPause()

    progressSubject
        .subscribe { inProgress ->
          playersRecycler.isLayoutFrozen = inProgress
          courtNumberView.isEnabled = !inProgress
          delayTimeView.isEnabled = !inProgress
        }
        .disposeOnPause()

    service.getPlayers()
        .doOnSubscribe { playersAnimator.displayedChildId = R.id.pb_reservation_players }
        .map { response ->
          response.error to (response.players?.filter { it.courtNumber == null } ?: emptyList())
        }
        .doOnSuccess { (_, players) ->
          playersAnimator.displayedChildId = if (players.isNullOrEmpty()) {
            R.id.ll_reservation_empty
          } else {
            R.id.rv_reservation_players_recycler
          }
        }
        .subscribe { (error, players) ->
          error?.also { Toast.makeText(view.context, it, LENGTH_LONG).show() }
          adapter.setPlayers(players)
        }
        .disposeOnPause()

    submitButton.clicks()
        .switchMapSingle {
          service.registerCourt(
              courtNumber = courtNumberView.text.toString().toInt(),
              players = adapter.selectedPlayers,
              delayMinutes = if (delayTimeView.text.isBlank()) 0 else delayTimeView.text.toString().toInt()
          )
          .doOnSubscribe { progressSubject.onNext(true) }
        }
        .subscribe { response ->
          response.error
              ?.also {
                progressSubject.onNext(false)
                Toast.makeText(view.context, it, LENGTH_LONG).show()
              }
              ?: activity?.onBackPressed()
        }
        .disposeOnPause()
  }
}