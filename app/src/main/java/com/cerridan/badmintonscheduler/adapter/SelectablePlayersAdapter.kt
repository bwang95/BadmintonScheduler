package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.view.View
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.view.SelectablePlayerItemView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SelectablePlayersAdapter(context: Context) : BaseRecyclerViewAdapter(context) {
  private val players = mutableListOf<Player>()
  private val selectedNames = hashSetOf<String>()
  private val selectedPlayersSubject = PublishSubject.create<Pair<String, Boolean>>()

  val observablePlayerSelections: Observable<Pair<String, Boolean>> get() = selectedPlayersSubject
  val selectedPlayers get() = players.filter { selectedNames.contains(it.name) }

  fun setPlayers(players: List<Player>) {
    this.players.clear()
    this.players += players

    selectedNames.retainAll(players.map(Player::name))

    notifyDataSetChanged()
  }

  override fun getLayoutForViewType(viewType: Int) = R.layout.item_player_selectable

  override fun onViewAttachedToWindow(holder: ViewHolder, view: View, position: Int) {
    val player = players[position]
    (view as SelectablePlayerItemView).apply {
      bind(player, selectedNames.contains(player.name))
      clicks()
          .map { player }
          .subscribe {
            isChecked = if (selectedNames.contains(it.name)) {
              selectedNames.remove(it.name)
              false
            } else {
              selectedNames.add(it.name)
              true
            }
            selectedPlayersSubject.onNext(it.name to isChecked)
          }
          .disposeOnRecycle(holder)
    }
  }

  override fun getItemCount() = players.size
}