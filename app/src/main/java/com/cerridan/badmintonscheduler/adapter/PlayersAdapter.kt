package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.view.PlayerItemView
import com.jakewharton.rxbinding2.view.longClicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PlayersAdapter(context: Context) : BaseRecyclerViewAdapter(context) {
  private val players = mutableListOf<Player>()
  private val playerLongPressSubject = PublishSubject.create<Player>()

  val playerLongPresses: Observable<Player> get() = playerLongPressSubject

  init { setHasStableIds(true) }

  fun setPlayers(players: List<Player>) {
    this.players.clear()
    this.players += players
    notifyDataSetChanged()
  }

  override fun getLayoutForViewType(viewType: Int) = R.layout.item_player

  override fun onViewAttachedToWindow(holder: ViewHolder, view: View, position: Int) =
      (view as PlayerItemView).run {
        val player = players[position]
        bind(player)
        longClicks()
            .map { player }
            .subscribe(playerLongPressSubject::onNext)
            .disposeOnRecycle(holder)
      }

  override fun getItemCount() = players.size

  override fun getItemId(position: Int) = players[position].name.hashCode().toLong()
}