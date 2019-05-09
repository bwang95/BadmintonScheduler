package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.view.PlayerItemView

class PlayersAdapter(context: Context) : BaseRecyclerViewAdapter(context) {
  private val players = mutableListOf<Player>()

  fun setPlayers(players: List<Player>) {
    this.players.clear()
    this.players += players
    notifyDataSetChanged()
  }

  override fun getLayoutForViewType(viewType: Int) = R.layout.item_player

  override fun onViewAttachedToWindow(view: View, position: Int) =
      (view as PlayerItemView).run { bind(players[position]) }

  override fun getItemCount() = players.size
}