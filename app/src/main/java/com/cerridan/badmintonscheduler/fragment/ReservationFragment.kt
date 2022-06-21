package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.cerridan.badmintonscheduler.ui.SelectablePlayerItem
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.IN_PROGRESS
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.NOT_STARTED
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.SUCCESS

class ReservationFragment: BaseComposeFragment<ReservationsViewModel>() {
  override val viewModel: ReservationsViewModel by viewModels {
    DaggerInjector.appComponent.viewModelFactory()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.refresh()
    viewModel.errors.observe(viewLifecycleOwner) { event ->
      event.value?.let { Toast.makeText(view.context, it, LENGTH_LONG).show() }
    }

    (view as ComposeView).setContent {
      var delayTime by remember { mutableStateOf("") }
      var courtNumber by remember { mutableStateOf("") }
      val selectedPlayers = remember { mutableStateMapOf<String, Unit>() }

      LaunchedEffect(viewModel.requestState) {
        if (viewModel.requestState == SUCCESS) activity?.onBackPressed()
      }

      Column {
        Column(Modifier.shadow(dimensionResource(R.dimen.global_elevation))) {
          Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.reservation_court_number))

            TextField(
              value = courtNumber,
              onValueChange = { courtNumber = it }
            )
          }

          Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.reservation_delay_time))

            TextField(
              value = delayTime,
              onValueChange = { delayTime = it }
            )
          }
        }

        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          val availablePlayers = viewModel.availablePlayers
          when {
            viewModel.requestState == IN_PROGRESS -> CircularProgressIndicator()
            availablePlayers.isNullOrEmpty() -> {
              Icon(
                painter = painterResource(R.drawable.icon_shuttle),
                contentDescription = null
              )
              Text(stringResource(R.string.reservation_players_empty))
            }
            else -> LazyColumn {
              items(availablePlayers, key = Player::name) { player ->
                SelectablePlayerItem(
                  name = player.name,
                  password = player.password,
                  checked = selectedPlayers.containsKey(player.name),
                  onCheckedChanged = { checked ->
                    if (checked) {
                      selectedPlayers[player.name] = Unit
                    } else {
                      selectedPlayers.remove(player.name)
                    }
                  }
                )
              }
            }
          }
        }
        
        Button(
          enabled = viewModel.requestState == NOT_STARTED,
          onClick = {
            viewModel.submitReservation(
              courtNumber = courtNumber.toInt(),
              players = emptyList(), // tODO
              delayMinutes = delayTime
                .takeIf(String::isNotBlank)
                ?.toInt()
                ?: 0
            )
          },
          content = { Text(stringResource(R.string.reservation_register)) }
        )
      }
    }
  }
}