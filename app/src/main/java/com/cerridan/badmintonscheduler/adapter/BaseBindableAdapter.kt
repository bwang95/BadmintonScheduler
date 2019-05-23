package com.cerridan.badmintonscheduler.adapter

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class BaseBindableAdapter<T>(context: Context) : BaseAdapter() {
  private val inflater = LayoutInflater.from(context)

  final override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
      (convertView ?: inflater.inflate(getLayout(position, getItemViewType(position)), parent, false))
          .also { bindView(it, position) }

  final override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View =
      getView(position, convertView, parent)

  abstract override fun getItem(position: Int): T

  @LayoutRes abstract fun getLayout(position: Int, viewType: Int): Int

  abstract fun bindView(view: View, position: Int)
}