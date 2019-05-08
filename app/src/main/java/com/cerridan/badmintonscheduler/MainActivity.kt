package com.cerridan.badmintonscheduler

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.cerridan.badmintonscheduler.fragment.SummaryFragment

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    supportFragmentManager.beginTransaction()
        .add(R.id.fl_main_fragment_container, SummaryFragment())
        .commit()
  }

  override fun onStop() {
    super.onStop()
  }
}
