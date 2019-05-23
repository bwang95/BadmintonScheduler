package com.cerridan.badmintonscheduler.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.util.bindView
import com.squareup.phrase.Phrase

class VersionItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
  private val versionNameView: TextView by bindView(R.id.tv_version_version)

  fun bind(version: String) {
    versionNameView.text = Phrase.from(this, R.string.version_name)
        .put("version", version)
        .format()
  }
}