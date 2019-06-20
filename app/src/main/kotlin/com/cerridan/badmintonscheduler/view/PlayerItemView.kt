package com.cerridan.badmintonscheduler.view

import android.content.Context
import android.support.v4.content.res.ResourcesCompat.getColor
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.util.bindView

class PlayerItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
  private val nameView: TextView by bindView(R.id.tv_player_name)
  private val passwordView: TextView by bindView(R.id.tv_player_password)
  private val courtView: TextView by bindView(R.id.tv_player_court)

  fun bindAsHeader() {
    isEnabled = false
    setBackgroundColor(getColor(resources, R.color.icon_color, null))
    nameView.setText(R.string.player_name_header)
    passwordView.setText(R.string.player_password_header)
    courtView.setText(R.string.player_court_header)
  }

  fun bind(player: Player) {
    isEnabled = true
    nameView.text = player.name
    passwordView.text = player.password
    courtView.text = player.courtNumber?.toString() ?: ""
  }
}