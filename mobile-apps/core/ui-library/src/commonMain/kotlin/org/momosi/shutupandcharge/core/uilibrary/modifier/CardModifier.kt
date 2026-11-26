package org.momosi.shutupandcharge.core.uilibrary.modifier

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.Shapes as AppShape

@Composable
fun Modifier.card(shape: Shape = AppShape.CardShape): Modifier = this
    .clip(shape)
    .background(AppTheme.colors.surfaceContainerLowest, shape)
