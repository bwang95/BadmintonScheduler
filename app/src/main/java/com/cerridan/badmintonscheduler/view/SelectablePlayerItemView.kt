package com.cerridan.badmintonscheduler.view

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.util.bindView

class SelectablePlayerItemView(
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {
  private val checkBox: CheckBox by bindView(R.id.cb_player_selectable_checkbox)
  private val nameView: TextView by bindView(R.id.tv_player_selectable_name)
  private val passwordView: TextView by bindView(R.id.tv_player_selectable_password)

  var isChecked: Boolean
    get() = checkBox.isChecked
    set(value) { checkBox.isChecked = value }

  fun bind(player: Player, selected: Boolean) {
    nameView.text = player.name
    passwordView.text = player.password
    checkBox.isChecked = selected
  }
}