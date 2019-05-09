package com.cerridan.badmintonscheduler.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.PlayersAdapter
import com.cerridan.badmintonscheduler.api.BadmintonService
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.view.PlayerItemView
import javax.inject.Inject

class PlayersFragment: BaseFragment(R.layout.fragment_players) {
  private val headerView: PlayerItemView by bindView(R.id.pv_players_header)
  private val playersRecycler: RecyclerView by bindView(R.id.rv_players_recycler)

  @Inject lateinit var service: BadmintonService

  init { DaggerInjector.appComponent.inject(this) }

  override fun onResume(view: View) {
    super.onResume(view)

    headerView.bindAsHeader()

    val adapter = PlayersAdapter(view.context)
    playersRecycler.layoutManager = LinearLayoutManager(context)
    playersRecycler.adapter = adapter

    service.getPlayers()
        .subscribe { response -> response.players?.let(adapter::setPlayers) }
        .disposeOnPause()
  }
}