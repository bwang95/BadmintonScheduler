package com.cerridan.badmintonscheduler.fragment

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.cerridan.badmintonscheduler.R

enum class DrawerNavigableFragmentDescriptor(
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
  COURTS(R.string.courts_title, R.drawable.icon_courts),
  PLAYERS(R.string.players_title, R.drawable.icon_player)
}