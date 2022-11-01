package com.cerridan.badmintonscheduler.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.cerridan.badmintonscheduler.R
import com.cerridan.badmintonscheduler.util.GlobalPadding

@Composable
fun VersionItem(
  modifier: Modifier = Modifier,
  version: String
) = Row(
  modifier = modifier.padding(GlobalPadding),
  horizontalArrangement = Arrangement.spacedBy(GlobalPadding),
  verticalAlignment = Alignment.CenterVertically
) {
  ResourcesCompat.getDrawable(LocalContext.current.resources, R.drawable.icon_version_drawer, null)
      ?.toBitmap()
      ?.asImageBitmap()
      ?.let {
        Icon(
            modifier = Modifier.size(dimensionResource(R.dimen.version_icon_size)),
            bitmap = it,
            contentDescription = stringResource(R.string.drawer_item_content_description)
        )
      }

  Column {
    Text(
      modifier = Modifier.padding(bottom = dimensionResource(R.dimen.global_padding_half)),
      text = stringResource(R.string.app_name),
      style = MaterialTheme.typography.body2
    )

    Text(
      text = stringResource(R.string.version_name, version),
      style = MaterialTheme.typography.caption
    )
  }
}