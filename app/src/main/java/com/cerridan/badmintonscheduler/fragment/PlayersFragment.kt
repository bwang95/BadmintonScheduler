package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.dialog.AddPlayerFragment
import com.cerridan.badmintonscheduler.dialog.RemovePlayerFragment
import com.cerridan.badmintonscheduler.ui.PlayerItem
import com.cerridan.badmintonscheduler.util.GlobalPadding
import com.cerridan.badmintonscheduler.util.backstackForegroundState
import com.cerridan.badmintonscheduler.util.showDialog
import com.cerridan.badmintonscheduler.viewmodel.PlayersViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlayersFragment : BaseComposeFragment<PlayersViewModel>() {
  override val viewModel: PlayersViewModel by viewModels {
    DaggerInjector.appComponent.viewModelFactory()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lifecycleScope.launch {
      backstackForegroundState
        .onEach { delay(250L) }
        .filter { it }
        .collect { viewModel.refresh() }
    }

    viewModel.errors.observe(viewLifecycleOwner) { event ->
      event.value?.let { Toast.makeText(view.context, it, LENGTH_LONG).show() }
    }
  }

  @Composable
  @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
  override fun Content() = Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showDialog(AddPlayerFragment()) },
        content = { Icon(painterResource(R.drawable.icon_add), contentDescription = "Add") }
      )
    }
  ) {
    val players = viewModel.players
    val isLoading = viewModel.isLoading
    val isRefreshing by remember { derivedStateOf { players != null && isLoading } }

    val pullRefreshState = rememberPullRefreshState(
      refreshing = isRefreshing,
      onRefresh = { viewModel.refresh(forceUpdate = true) }
    )

    Box(
      Modifier
        .fillMaxSize()
        .padding(it)
        .pullRefresh(pullRefreshState)
    ) {
      when {
        players == null -> Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          content = { CircularProgressIndicator() }
        )

        // Vertical scrolling necessary for pull-to-refresh
        players.isEmpty() -> Column(
          modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            modifier = Modifier.padding(bottom = GlobalPadding),
            painter = painterResource(R.drawable.icon_shuttle),
            contentDescription = null
          )
          Text(stringResource(R.string.players_empty))
        }

        else -> Column(
          Modifier
            .fillMaxSize()
            .padding(it)
        ) {
          PlayerItem(
            modifier = Modifier.shadow(elevation = GlobalPadding),
            name = stringResource(R.string.player_name_header),
            password = stringResource(R.string.player_password_header),
            court = stringResource(R.string.player_court_header)
          )

          LazyColumn {
            items(players, key = Player::name) { player ->
              Column(Modifier.animateItemPlacement()) {
                Divider()
                PlayerItem(
                  name = player.name,
                  password = player.password,
                  court = player.court ?: "",
                  onClick = { showDialog(RemovePlayerFragment.create(player.name)) }
                )
              }
            }
          }
        }
      }

      PullRefreshIndicator(
        modifier = Modifier.align(Alignment.TopCenter),
        refreshing = isRefreshing,
        state = pullRefreshState
      )
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.refresh()
  }
}
