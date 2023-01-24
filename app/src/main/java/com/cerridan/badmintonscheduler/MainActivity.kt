package com.cerridan.badmintonscheduler

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.cerridan.badmintonscheduler.dialog.EndSessionFragment
import com.cerridan.badmintonscheduler.fragment.CourtsFragment
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor.COURTS
import com.cerridan.badmintonscheduler.fragment.DrawerNavigableFragmentDescriptor.PLAYERS
import com.cerridan.badmintonscheduler.fragment.PlayersFragment
import com.cerridan.badmintonscheduler.ui.AppTheme
import com.cerridan.badmintonscheduler.ui.DrawerItem
import com.cerridan.badmintonscheduler.ui.VersionItem
import com.cerridan.badmintonscheduler.util.KEY_BACKSTACK

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
        Column {
          VersionItem(version = BuildConfig.VERSION_NAME)
          DrawerNavigableFragmentDescriptor.values().forEach {
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
          Spacer(Modifier.weight(1f))
          DrawerItem(
            modifier = Modifier.clickable { showDialog(EndSessionFragment()) },
            iconRes = R.drawable.icon_end_session,
            textRes = R.string.end_session_title
          )
        }
      }
    }

    supportFragmentManager.beginTransaction()
        .add(R.id.fl_main_fragment_container, CourtsFragment())
        .commit()
  }

  internal fun showDialog(dialog: DialogFragment, source: Fragment? = null) = supportFragmentManager
    .let { dialog.show(it.beginTransaction().addToBackStack(null), null) }
    .let { source?.arguments = (source?.arguments ?: Bundle()).apply { putInt(KEY_BACKSTACK, it) } }

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
