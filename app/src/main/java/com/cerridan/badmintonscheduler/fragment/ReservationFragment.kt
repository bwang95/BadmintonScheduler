package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import android.widget.ViewAnimator
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.SelectablePlayersAdapter
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.displayedChildId
import com.cerridan.badmintonscheduler.util.hideKeyboard
import com.cerridan.badmintonscheduler.util.requestFocusAndShowKeyboard
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import javax.inject.Inject

class ReservationFragment : BaseFragment(R.layout.fragment_reservation) {
  private val courtNumberView: EditText by bindView(R.id.et_reservation_court_number)
  private val delayTimeView: EditText by bindView(R.id.et_reservation_delay_time)
  private val playersAnimator: ViewAnimator by bindView(R.id.va_reservation_players_animator)
  private val playersRecycler: RecyclerView by bindView(R.id.rv_reservation_players_recycler)
  private val submitButton: Button by bindView(R.id.b_reservation_submit)

  private lateinit var adapter: SelectablePlayersAdapter

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

    Disposables.fromAction { courtNumberView.hideKeyboard() }
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
          when {
            courtNumberView.text.isNullOrBlank() -> {
              courtNumberView.error = resources.getString(R.string.reservation_court_number_error)
              Single.never()
            }
            adapter.selectedPlayers.isEmpty() -> {
              Toast.makeText(view.context, R.string.reservation_players_error, LENGTH_SHORT).show()
              Single.never()
            }
            else -> service.registerCourt(
                courtNumber = courtNumberView.text.toString().toInt(),
                players = adapter.selectedPlayers,
                delayMinutes = if (delayTimeView.text.isBlank()) 0 else delayTimeView.text.toString().toInt()
            )
            .doOnSubscribe { /* TODO loading state */ }
          }
        }
        .subscribe { response ->
          response.error
              ?.also { Toast.makeText(view.context, it, LENGTH_LONG).show() }
              ?: activity?.onBackPressed()
        }
        .disposeOnPause()
  }
}