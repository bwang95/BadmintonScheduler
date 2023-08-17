package com.cerridan.badmintonscheduler.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.cerridan.badmintonscheduler.ui.AppTheme

abstract class BaseComposeFragment<VM : ViewModel> : Fragment() {
  protected abstract val viewModel: VM

  final override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = ComposeView(requireContext()).apply { isClickable = true }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (view as ComposeView).setContent { AppTheme { Content() } }
  }

  @Composable
  protected abstract fun Content()
}
