package com.cerridan.badmintonscheduler

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.cerridan.badmintonscheduler.fragment.CourtsFragment
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor.COURTS
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor.PLAYERS
import com.cerridan.badmintonscheduler.fragment.PlayersFragment
import com.cerridan.badmintonscheduler.ui.AppTheme
import com.cerridan.badmintonscheduler.ui.DrawerItem
import com.cerridan.badmintonscheduler.ui.VersionItem

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val toolbar: Toolbar = findViewById(R.id.tb_main_toolbar)
    val drawerLayout: DrawerLayout = findViewById(R.id.dl_main_content)
    val drawer: ComposeView = findViewById(R.id.rv_main_nav_drawer)

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

    drawer.setContent {
      AppTheme {
        LazyColumn {
          item { VersionItem(version = BuildConfig.VERSION_NAME) }
          items(DrawerNavigableFragmentDescriptor.values()) {
            DrawerItem(
                modifier = Modifier.clickable {
                  supportFragmentManager.beginTransaction()
                      .replace(R.id.fl_main_fragment_container, it.newFragment())
                      .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                      .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                      .commit()
                  drawerLayout.close()
                },
                iconRes = it.icon,
                textRes = it.label
            )
          }
        }
      }
    }

    supportFragmentManager.beginTransaction()
        .add(R.id.fl_main_fragment_container, CourtsFragment())
        .commit()
  }

  override fun onBackPressed() = with(supportFragmentManager) {
    when {
      backStackEntryCount > 1 -> popBackStack()
      else -> super.onBackPressed()
    }
  }

  private fun DrawerNavigableFragmentDescriptor.newFragment(): Fragment = when (this) {
    COURTS -> CourtsFragment()
    PLAYERS -> PlayersFragment()
  }
}
