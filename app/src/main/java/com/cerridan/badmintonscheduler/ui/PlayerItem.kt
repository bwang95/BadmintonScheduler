package com.cerridan.badmintonscheduler.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.cerridan.badmintonscheduler.R.dimen

@Composable
fun PlayerItem(
  modifier: Modifier = Modifier,
  name: String,
  password: String,
  court: String,
  onClick: () -> Unit = {}
) {
  Row(
      modifier = modifier
          .fillMaxWidth()
          .background(MaterialTheme.colors.background)
          .clickable(onClick = onClick)
          .padding(dimensionResource(dimen.global_padding))
  ) {
    Text(modifier = Modifier.weight(4f), text = name)
    Text(modifier = Modifier.weight(4f), text = password)
    Text(modifier = Modifier.weight(2f), text = court)
  }
}
