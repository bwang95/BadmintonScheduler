package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.dialog.CourtActionsFragment
import com.cerridan.badmintonscheduler.ui.CourtItem
import com.cerridan.badmintonscheduler.util.backstackForegroundState
import com.cerridan.badmintonscheduler.util.push
import com.cerridan.badmintonscheduler.util.showDialog
import com.cerridan.badmintonscheduler.viewmodel.CourtsViewModel
import com.cerridan.badmintonscheduler.viewmodel.CourtsViewModel.Court
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date

class CourtsFragment : BaseComposeFragment<CourtsViewModel>() {

  override val viewModel: CourtsViewModel by viewModels {
    DaggerInjector.appComponent.viewModelFactory()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lifecycleScope.launch {
      backstackForegroundState
        .onEach { delay(250L) }
        .filter { it }
        .collect {
          Log.e("Courts", "Refresh VM")
          viewModel.refresh()
        }
    }

    lifecycleScope.launch {
      viewModel.errors.collect { error ->
        Toast.makeText(view.context, error, LENGTH_LONG).show()
      }
    }
  }

  @Composable
  @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
  override fun Content() = Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        onClick = { push(ReservationFragment()) },
        content = { Icon(painterResource(R.drawable.icon_add), contentDescription = "Add") }
      )
    }
  ) {
    val now = Date()
    val courts = viewModel.courts
    val isLoading = viewModel.isLoading
    val isRefreshing by remember { derivedStateOf { courts != null && isLoading } }
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
        courts == null -> Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
          content = { CircularProgressIndicator() }
        )

        // Vertical scrolling necessary for pull-to-refresh
        courts.isEmpty() -> Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(painterResource(R.drawable.icon_court), contentDescription = null)
          Text(stringResource(R.string.courts_empty))
        }

        else -> LazyColumn(Modifier.fillMaxSize()) {
          items(courts, key = Court::name) { court ->
            Column(Modifier.animateItemPlacement()) {
              Divider()
              CourtItem(
                modifier = Modifier.clickable {
                  showDialog(
                    CourtActionsFragment.create(court.name, court.reservations.first().token)
                  )
                },
                court = court,
                now = now
              )
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
