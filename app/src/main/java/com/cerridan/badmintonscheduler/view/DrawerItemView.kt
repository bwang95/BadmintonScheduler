package com.cerridan.badmintonscheduler.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragment
import com.cerridan.badmintonscheduler.util.bindView

class DrawerItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
  private val iconView: ImageView by bindView(R.id.iv_drawer_icon)
  private val titleView: TextView by bindView(R.id.tv_drawer_title)

  fun bind(info: DrawerNavigableFragment) {
    iconView.setImageResource(info.icon)
    titleView.setText(info.label)
  }
}