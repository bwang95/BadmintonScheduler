package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import androidx.annotation.StringRes
import android.view.View
import android.widget.TextView
import com.cerridan.badmintonscheduler.R

class PasswordsAdapter(private val context: Context) : BaseBindableAdapter<String>(context) {
  private var placeholderPosition = -1
  private val rows = mutableListOf<String>()

  fun setData(@StringRes placeholder: Int, values: List<String>) {
    rows.clear()
    placeholderPosition = 0
    rows += context.getString(placeholder)
    rows += values
    notifyDataSetChanged()
  }

  fun isPlaceholder(position: Int) = placeholderPosition == position

  override fun getItem(position: Int) = rows[position]

  override fun getLayout(position: Int, viewType: Int) = R.layout.item_password

  override fun bindView(view: View, position: Int) { (view as TextView).text = rows[position] }

  override fun getItemId(position: Int) = rows[position].hashCode().toLong()

  override fun getCount() = rows.size
}