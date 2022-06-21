package com.cerridan.badmintonscheduler.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.cerridan.badmintonscheduler.R

@Composable
fun SelectablePlayerItem(
  modifier: Modifier = Modifier,
  name: String,
  password: String,
  checked: Boolean,
  onCheckedChanged: (Boolean) -> Unit
) = Row(
  modifier = modifier.padding(
    horizontal = dimensionResource(R.dimen.global_padding),
    vertical = dimensionResource(R.dimen.global_padding_quarter)
  ),
  verticalAlignment = Alignment.CenterVertically
) {
  Checkbox(
    checked = checked,
    onCheckedChange = onCheckedChanged
  )

  Text(
    modifier = Modifier.weight(1f),
    text = name
  )

  Text(
    modifier = Modifier.weight(1f),
    text = password
  )
}