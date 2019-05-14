package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.cerridan.badmintonscheduler.util.displayedChildId
import com.cerridan.badmintonscheduler.util.requestFocusAndShowKeyboard
import com.jakewharton.rxbinding2.view.clicks
import javax.inject.Inject

class RegistrationFragment : BaseFragment(R.layout.fragment_registration) {
  private val courtNumberView: EditText by bindView(R.id.et_registration_court_number)
  private val delayTimeView: EditText by bindView(R.id.et_registration_delay_time)
  private val playersAnimator: ViewAnimator by bindView(R.id.va_registration_players_animator)
  private val playersRecycler: RecyclerView by bindView(R.id.rv_registration_players_recycler)
  private val submitButton: Button by bindView(R.id.b_registration_submit)

  private lateinit var adapter: SelectablePlayersAdapter

  @Inject lateinit var service: BadmintonService

  init { DaggerInjector.appComponent.inject(this) }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    adapter = SelectablePlayersAdapter(view.context)
    playersRecycler.layoutManager = LinearLayoutManager(view.context)
    playersRecycler.adapter = adapter
  }

  override fun onResume(view: View) {
    super.onResume(view)

    courtNumberView.requestFocusAndShowKeyboard()

    service.getPlayers()
        .doOnSubscribe { playersAnimator.displayedChildId = R.id.pb_registration_players }
        .map { response ->
          response.error to (response.players?.filter { it.courtNumber == null } ?: emptyList())
        }
        .doOnSuccess { (_, players) ->
          playersAnimator.displayedChildId = if (players.isNullOrEmpty()) {
            R.id.ll_registration_empty
          } else {
            R.id.rv_registration_players_recycler
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
          .doOnSubscribe { /* TODO loading state */ }
        }
        .subscribe { response ->
          response.error
              ?.also { Toast.makeText(view.context, it, LENGTH_LONG).show() }
              ?: activity?.onBackPressed()
        }
        .disposeOnPause()
  }
}