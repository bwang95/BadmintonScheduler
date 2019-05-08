package com.cerridan.badmintonscheduler.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.adapter.PlayersAdapter
import com.cerridan.badmintonscheduler.model.DataModel
import com.cerridan.badmintonscheduler.util.bindView
import com.cerridan.badmintonscheduler.view.PlayerItemView
import javax.inject.Inject

class PlayersFragment: BaseFragment(R.layout.fragment_players) {
  private val headerView: PlayerItemView by bindView(R.id.pv_players_header)
  private val playersRecycler: RecyclerView by bindView(R.id.rv_players_recycler)

  @Inject lateinit var model: DataModel

  init {

  }

  override fun onStart(view: View) {
    super.onStart()

    headerView.bindAsHeader()

    val adapter = PlayersAdapter(view.context)
    playersRecycler.layoutManager = LinearLayoutManager(context)
    playersRecycler.adapter = adapter

    model.observablePlayers
        .subscribe(adapter::setPlayers)
        .disposeOnStop()
  }
}