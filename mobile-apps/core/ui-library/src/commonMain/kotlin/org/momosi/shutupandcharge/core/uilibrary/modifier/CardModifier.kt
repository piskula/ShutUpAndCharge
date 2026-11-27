package org.momosi.shutupandcharge.core.uilibrary.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.momosi.shutupandcharge.core.uilibrary.lazylist.ListItemPosition
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.Shapes as AppShape

/**
 * Content card: `surfaceBright`, which Material 3 keeps brighter than the `surfaceContainer` page in both
 * light and dark theme (unlike the `surfaceContainer*` scale, which darkens in light and lightens in dark).
 */
@Composable
fun Modifier.card(shape: Shape = AppShape.CardShape): Modifier = this
    .clip(shape)
    .background(AppTheme.colors.surfaceBright, shape)

/** Card background for one row of a card that spans several lazy list items. */
@Composable
fun Modifier.card(position: ListItemPosition): Modifier = card(
    when (position) {
        ListItemPosition.Single -> AppShape.CardShape
        ListItemPosition.First -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ListItemPosition.Middle -> RectangleShape
        ListItemPosition.Last -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    },
)
