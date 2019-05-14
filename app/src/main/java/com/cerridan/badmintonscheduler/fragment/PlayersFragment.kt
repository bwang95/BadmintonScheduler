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
import com.cerridan.badmintonscheduler.adapter.PlayersAdapter
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.dialog.AddPlayerFragment
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.util.displayedChildId
import com.cerridan.badmintonscheduler.util.showDialog
import com.cerridan.badmintonscheduler.view.PlayerItemView
import com.jakewharton.rxbinding2.view.clicks
import javax.inject.Inject

class PlayersFragment: BaseFragment(R.layout.fragment_players) {
  private val animator: ViewAnimator by bindView(R.id.va_players_animator)
  private val headerView: PlayerItemView by bindView(R.id.pv_players_header)
  private val playersRecycler: RecyclerView by bindView(R.id.rv_players_recycler)
  private val addPlayerButton: FloatingActionButton by bindView(R.id.fab_players_add)

  @Inject lateinit var service: BadmintonService

  private lateinit var adapter: PlayersAdapter

  init { DaggerInjector.appComponent.inject(this) }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    headerView.bindAsHeader()

    adapter = PlayersAdapter(view.context)
    playersRecycler.layoutManager = LinearLayoutManager(view.context)
    playersRecycler.adapter = adapter
  }

  override fun onResume(view: View) {
    super.onResume(view)

    service.getPlayers()
        .doOnSubscribe { animator.displayedChildId = R.id.pb_players_progress }
        .doOnSuccess {
          animator.displayedChildId = if (it.players.isNullOrEmpty()) {
            R.id.ll_players_empty
          } else {
            R.id.rv_players_content
          }
        }
        .subscribe { response ->
          response.error?.also { Toast.makeText(view.context, it, LENGTH_LONG).show() }
          response.players?.let(adapter::setPlayers)
        }
        .disposeOnPause()

    addPlayerButton.clicks()
        .subscribe { showDialog(AddPlayerFragment()) }
        .disposeOnPause()
  }
}