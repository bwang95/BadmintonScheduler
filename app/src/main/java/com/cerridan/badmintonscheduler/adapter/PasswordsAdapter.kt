package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.StringRes
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cerridan.badmintonscheduler.R.layout

class PasswordsAdapter(private val context: Context) : BaseAdapter() {
  private val inflater = LayoutInflater.from(context)
  private var placeholderPosition = -1
  private val rows = mutableListOf<String>()

  fun setData(@StringRes placeholder: Int, values: List<String>) {
    rows.clear()
    placeholderPosition = 0
    rows += context.getString(placeholder)
    rows += values
    notifyDataSetChanged()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
    (convertView ?: inflater.inflate(layout.item_password, parent, false))
        .also { (it as TextView).text = rows[position] }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View =
    getView(position, convertView, parent)

  fun isPlaceholder(position: Int) = placeholderPosition == position

  override fun getItem(position: Int) = rows[position]

  override fun getItemId(position: Int) = rows[position].hashCode().toLong()

  override fun getCount() = rows.size
}