package com.cerridan.badmintonscheduler.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.cerridan.badmintonscheduler.R

@Composable
fun DrawerItem(
  modifier: Modifier = Modifier,
  @DrawableRes iconRes: Int,
  @StringRes textRes: Int
) = Row(
  modifier = modifier.padding(dimensionResource(R.dimen.global_padding)),
  verticalAlignment = Alignment.CenterVertically
) {
  Icon(
    painter = painterResource(iconRes),
    contentDescription = null
  )

  Text(
    modifier = Modifier.padding(start = dimensionResource(R.dimen.global_padding)),
    text = stringResource(textRes),
    style = MaterialTheme.typography.h2
  )
}