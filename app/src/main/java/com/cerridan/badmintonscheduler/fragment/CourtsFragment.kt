package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.dagger.DaggerInjector
import com.cerridan.badmintonscheduler.dialog.CourtActionsFragment
import com.cerridan.badmintonscheduler.util.observableForegroundBackstackState
import com.cerridan.badmintonscheduler.util.push
import com.cerridan.badmintonscheduler.util.showDialog
import com.cerridan.badmintonscheduler.ui.CourtItem
import com.cerridan.badmintonscheduler.viewmodel.CourtsViewModel
import com.cerridan.badmintonscheduler.viewmodel.CourtsViewModel.Court
import io.reactivex.rxjava3.disposables.SerialDisposable
import java.util.Date

class CourtsFragment : BaseComposeFragment<CourtsViewModel>() {

  private val foregroundDisposable = SerialDisposable()
  override val viewModel: CourtsViewModel by viewModels {
    DaggerInjector.appComponent.viewModelFactory()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observableForegroundBackstackState
        .filter { it }
        .subscribe { viewModel.refresh() }
        .let(foregroundDisposable::set)

    viewModel.errors.observe(viewLifecycleOwner) { event ->
      event.value?.let { Toast.makeText(view.context, it, LENGTH_LONG).show() }
    }

    (view as ComposeView).setContent {
      val now = Date()
      val courts = viewModel.courts

      Scaffold(
          content = {
            when {
              courts == null -> Column(
                  modifier = Modifier.fillMaxSize(),
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.CenterHorizontally,
                  content = { CircularProgressIndicator() }
              )

              courts.isEmpty() -> Column(
                  modifier = Modifier.fillMaxSize(),
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(painterResource(R.drawable.icon_court), stringResource(R.string.courts_empty))
                Text(stringResource(R.string.courts_empty))
              }

              else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(courts, key = Court::name) {
                  Divider()
                  CourtItem(
                    modifier = Modifier.clickable {
                      showDialog(CourtActionsFragment.create(it.name, it.reservations.first().token))
                    },
                    court = it,
                    now = now
                  )
                }
              }
            }
          },
          floatingActionButton = {
            FloatingActionButton(
                onClick = { push(ReservationFragment()) },
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
