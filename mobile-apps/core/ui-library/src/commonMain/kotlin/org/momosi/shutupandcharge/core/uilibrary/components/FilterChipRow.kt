package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Composable
fun <T> FilterChipRow(
    items: ImmutableList<T>,
    selectedItem: T,
    labelSelector: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = AppTheme.colors.surfaceContainer,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { item ->
            val selected = item == selectedItem
            FilterChip(
                selected = selected,
                onClick = { onSelect(item) },
                modifier = Modifier.testTag("filter_chip_$item"),
                label = {
                    Text(
                        text = labelSelector(item),
                        style = AppTheme.typography.labelLarge,
                    )
                },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Transparent,
                    labelColor = AppTheme.colors.onSurface,
                    selectedContainerColor = AppTheme.colors.primary,
                    selectedLabelColor = AppTheme.colors.onPrimary,
                ),
                border = if (selected) {
                    null
                } else {
                    BorderStroke(1.dp, AppTheme.colors.onSurfaceVariant)
                },
            )
        }
    }
}

@Preview
@Composable
private fun FilterChipRowPreview() {
    SuacTheme {
        FilterChipRow(
            items = persistentListOf("All", "Open", "In Progress", "Done"),
            selectedItem = "All",
            labelSelector = { it },
            onSelect = {},
        )
    }
}

@Preview
@Composable
private fun FilterChipRowSelectedPreview() {
    SuacTheme {
        FilterChipRow(
            items = persistentListOf("All", "Open", "In Progress", "Done"),
            selectedItem = "Open",
            labelSelector = { it },
            onSelect = {},
        )
    }
}
