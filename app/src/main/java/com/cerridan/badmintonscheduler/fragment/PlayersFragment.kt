package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.dialog.AddPlayerFragment
import com.cerridan.badmintonscheduler.dialog.RemovePlayerFragment
import com.cerridan.badmintonscheduler.util.observableForegroundBackstackState
import com.cerridan.badmintonscheduler.util.showDialog
import com.cerridan.badmintonscheduler.view.PlayerItem
import com.cerridan.badmintonscheduler.viewmodel.PlayersViewModel
import io.reactivex.rxjava3.disposables.SerialDisposable

class PlayersFragment : BaseComposeFragment<PlayersViewModel>() {

  private val foregroundDisposable = SerialDisposable()
  override val viewModel: PlayersViewModel by viewModels {
    DaggerInjector.appComponent.viewModelFactory()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observableForegroundBackstackState
        .subscribe { viewModel.refresh() }
        .let(foregroundDisposable::set)

    viewModel.errors.observe(this) { event ->
      event.value?.let { Toast.makeText(view.context, it, LENGTH_LONG).show() }
    }

    (view as ComposeView).setContent {
      val players = viewModel.players

      Scaffold(
          content = {
            when {
              players == null -> Column(
                  modifier = Modifier.fillMaxSize(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
              ) {
                CircularProgressIndicator()
              }

              players.isEmpty() -> Column(
                  modifier = Modifier.fillMaxSize(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
              ) {
                Icon(painterResource(R.drawable.icon_shuttle), contentDescription = "Shuttle")
                Text(stringResource(R.string.players_empty))
              }

              else -> Column(modifier = Modifier.fillMaxSize()) {
                PlayerItem(
                    modifier = Modifier.shadow(elevation = dimensionResource(R.dimen.global_elevation)),
                    name = stringResource(R.string.player_name_header),
                    password = stringResource(R.string.player_password_header),
                    court = stringResource(R.string.player_court_header),
                    onClick = {}
                )

                LazyColumn {
                  items(
                      players,
                      key = Player::name,
                      itemContent = {
                        Divider()
                        PlayerItem(
                            name = it.name,
                            password = it.password,
                            court = it.hasActiveReservation.toString(),
                            onClick = { showDialog(RemovePlayerFragment.create(it.name)) }
                        )
                      }
                  )
                }
              }
            }
          },
          floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog(AddPlayerFragment()) },
                content = { Icon(painterResource(R.drawable.icon_add), contentDescription = "Add") }
            )
          }
      )
    }
  }

  override fun onDestroyView() {
    foregroundDisposable.set(null)
    super.onDestroyView()
  }

  override fun onResume() {
    super.onResume()
    viewModel.refresh()
  }
}
