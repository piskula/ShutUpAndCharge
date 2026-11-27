package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.Shapes
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Immutable
sealed interface ChipVariant {
    data object Default : ChipVariant
    data object Tonal : ChipVariant
    data object Warning : ChipVariant
}

@Composable
fun Chip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    trailingIcon: ImageVector? = null,
    leadingIcon: ImageVector? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = AppTheme.typography.labelLarge,
            )
        },
        modifier = modifier,
        trailingIcon = trailingIcon?.let {
            {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        },
        shape = Shapes.CardShape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = AppTheme.colors.surfaceVariant,
            labelColor = AppTheme.colors.onSurfaceVariant,
            iconColor = AppTheme.colors.onSurfaceVariant,
            selectedContainerColor = AppTheme.colors.primaryContainer,
            selectedLabelColor = AppTheme.colors.onPrimaryContainer,
            selectedLeadingIconColor = AppTheme.colors.onPrimaryContainer,
            selectedTrailingIconColor = AppTheme.colors.onPrimaryContainer,
        ),
        border = null,
    )
}

@Composable
fun Chip(
    label: String,
    variant: ChipVariant,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    val (containerColor, contentColor) = when (variant) {
        ChipVariant.Default -> AppTheme.colors.surfaceVariant to AppTheme.colors.onSurfaceVariant
        ChipVariant.Tonal -> AppTheme.colors.primaryContainer to AppTheme.colors.onPrimaryContainer
        ChipVariant.Warning -> AppTheme.colors.warningContainer to AppTheme.colors.onWarningContainer
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .background(color = containerColor, shape = Shapes.CardShape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = label,
            style = AppTheme.typography.labelMedium,
            color = contentColor,
        )
    }
}

@Composable
fun OutlinedChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    trailingIcon: ImageVector? = null,
    leadingIcon: ImageVector? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = AppTheme.typography.labelLarge,
            )
        },
        modifier = modifier,
        trailingIcon = trailingIcon?.let {
            {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        },
        shape = Shapes.CardShape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            labelColor = AppTheme.colors.onSurfaceVariant,
            iconColor = AppTheme.colors.onSurfaceVariant,
            selectedContainerColor = AppTheme.colors.primaryContainer,
            selectedLabelColor = AppTheme.colors.onPrimaryContainer,
            selectedLeadingIconColor = AppTheme.colors.onPrimaryContainer,
            selectedTrailingIconColor = AppTheme.colors.onPrimaryContainer,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant,
        ),
    )
}

@Preview
@Composable
private fun ChipSurfacePreview() {
    SuacTheme {
        Chip(label = "Size", variant = ChipVariant.Default)
    }
}

@Preview
@Composable
private fun ChipTonalPreview() {
    SuacTheme {
        Chip(label = "v2.1.0 (210)", variant = ChipVariant.Tonal)
    }
}

@Preview
@Composable
private fun ChipWarningPreview() {
    SuacTheme {
        Chip(label = "SDK 28", variant = ChipVariant.Warning)
    }
}

@Preview
@Composable
private fun ChipClickableUnselectedPreview() {
    SuacTheme {
        Chip(label = "Size", onClick = {})
    }
}

@Preview
@Composable
private fun ChipClickableSelectedPreview() {
    SuacTheme {
        Chip(
            label = "Name",
            onClick = {},
            selected = true,
            trailingIcon = SuacIcons.Check,
        )
    }
}

@Preview
@Composable
private fun OutlinedChipUnselectedPreview() {
    SuacTheme {
        OutlinedChip(
            label = "Match: Any",
            onClick = {},
            trailingIcon = SuacIcons.ArrowDropDown,
        )
    }
}

@Preview
@Composable
private fun OutlinedChipSelectedPreview() {
    SuacTheme {
        OutlinedChip(
            label = "Match: All",
            onClick = {},
            selected = true,
            trailingIcon = SuacIcons.ArrowDropDown,
        )
    }
}
