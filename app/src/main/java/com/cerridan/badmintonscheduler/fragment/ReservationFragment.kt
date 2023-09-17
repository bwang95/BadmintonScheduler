package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.api.model.Player
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.ui.SelectablePlayerItem
import com.cerridan.badmintonscheduler.util.GlobalPadding
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.IN_PROGRESS
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.NOT_STARTED
import com.cerridan.badmintonscheduler.viewmodel.ReservationsViewModel.RequestState.SUCCESS
import kotlinx.coroutines.launch

class ReservationFragment : BaseComposeFragment<ReservationsViewModel>() {
  override val viewModel: ReservationsViewModel by viewModels {
    DaggerInjector.appComponent.viewModelFactory()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.refresh()

    lifecycleScope.launch {
      viewModel.errors.collect { error ->
        Toast.makeText(view.context, error, LENGTH_LONG).show()
      }
    }
  }

  @Composable
  override fun Content() = Column(
    modifier = Modifier
      .imePadding()
      .fillMaxSize()
      .background(MaterialTheme.colors.background),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    var delayTime by remember { mutableStateOf("") }
    var courtNumber by remember { mutableStateOf("") }
    val selectedPlayers = remember { mutableStateMapOf<Player, Unit>() }
    val isValidInput by remember {
      derivedStateOf { selectedPlayers.isNotEmpty() && courtNumber.isNotBlank() }
    }

    LaunchedEffect(viewModel.requestState) {
      if (viewModel.requestState == SUCCESS) activity?.onBackPressed()
    }

    Row(
      modifier = Modifier
        .shadow(GlobalPadding)
        .background(MaterialTheme.colors.background)
        .padding(GlobalPadding),
      horizontalArrangement = Arrangement.spacedBy(GlobalPadding)
    ) {
      TextField(
        modifier = Modifier.weight(0.5f),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Next
        ),
        maxLines = 1,
        value = courtNumber,
        label = { Text(stringResource(R.string.reservation_court_number)) },
        onValueChange = { courtNumber = it }
      )

      TextField(
        modifier = Modifier.weight(0.5f),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.None
        ),
        maxLines = 1,
        value = delayTime,
        label = { Text(stringResource(R.string.reservation_delay_time)) },
        onValueChange = { delayTime = it }
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      val availablePlayers = viewModel.availablePlayers
      when {
        viewModel.requestState == IN_PROGRESS -> CircularProgressIndicator()
        availablePlayers.isNullOrEmpty() -> {
          Icon(
            modifier = Modifier.padding(bottom = GlobalPadding),
            painter = painterResource(R.drawable.icon_shuttle),
            contentDescription = null
          )
          Text(stringResource(R.string.reservation_players_empty))
        }

        else -> LazyColumn(Modifier.fillMaxSize()) {
          items(availablePlayers, key = Player::name) { player ->
            SelectablePlayerItem(
              name = player.name,
              password = player.password,
              checked = selectedPlayers.containsKey(player),
              onCheckedChanged = { checked ->
                if (checked) {
                  selectedPlayers[player] = Unit
                } else {
                  selectedPlayers.remove(player)
                }
              }
            )

            Divider()
          }
        }
      }
    }

    Button(
      modifier = Modifier
        .padding(GlobalPadding)
        .fillMaxWidth(),
      enabled = viewModel.requestState == NOT_STARTED && isValidInput,
      onClick = {
        viewModel.submitReservation(
          courtNumber = courtNumber.toInt(),
          players = selectedPlayers.map { (name, _) -> name },
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