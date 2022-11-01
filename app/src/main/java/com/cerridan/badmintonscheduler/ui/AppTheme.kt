package com.cerridan.badmintonscheduler.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable fun AppTheme(content: @Composable () -> Unit) {
  val colorSet = if (isSystemInDarkTheme()) darkColors() else lightColors()
  CompositionLocalProvider(LocalContentColor provides colorSet.onBackground) {
    MaterialTheme(colors = colorSet, content = content)
  }
}