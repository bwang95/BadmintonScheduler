package com.cerridan.badmintonscheduler

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.cerridan.badmintonscheduler.adapter.DrawerAdapter
import com.cerridan.badmintonscheduler.fragment.CourtsFragment
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor.COURTS
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor.PLAYERS
import com.cerridan.badmintonscheduler.fragment.PlayersFragment
import com.cerridan.badmintonscheduler.util.bindView
import io.reactivex.rxjava3.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {
  private val toolbar: Toolbar by bindView(R.id.tb_main_toolbar)
  private val drawerLayout: DrawerLayout by bindView(R.id.dl_main_content)
  private val drawer: RecyclerView by bindView(R.id.rv_main_nav_drawer)

  private lateinit var adapter: DrawerAdapter
  private val disposables = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setSupportActionBar(toolbar)
    val toggle = ActionBarDrawerToggle(
        this,
        drawerLayout,
        toolbar,
        R.string.drawer_open,
        R.string.drawer_closed
    )
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    adapter = DrawerAdapter(this)
    drawer.layoutManager = LinearLayoutManager(this)
    drawer.adapter = adapter

    supportFragmentManager.beginTransaction()
        .add(R.id.fl_main_fragment_container, CourtsFragment())
        .commit()
  }

  override fun onResume() {
    super.onResume()

    adapter.fragmentDescriptorClicks
        .map {
          when (it) {
            COURTS -> CourtsFragment()
            PLAYERS -> PlayersFragment()
          }
        }
        .doOnNext { drawerLayout.closeDrawer(drawer) }
        .subscribe {
          supportFragmentManager.beginTransaction()
              .replace(R.id.fl_main_fragment_container, it)
              .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
              .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
              .commit()
        }
        .let(disposables::add)
  }

  override fun onPause() {
    disposables.clear()

    super.onPause()
  }

  override fun onBackPressed() = with(supportFragmentManager) {
    when {
      backStackEntryCount > 1 -> popBackStack()
      else -> super.onBackPressed()
    }
  }
}
