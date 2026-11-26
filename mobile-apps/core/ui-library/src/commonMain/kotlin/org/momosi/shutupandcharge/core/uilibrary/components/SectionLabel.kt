package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.onSurfaceVariant,
) {
    Text(
        text = text,
        style = AppTheme.typography.labelLarge,
        color = color,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun SectionLabelPreview() {
    SuacTheme {
        SectionLabel(text = "In Progress — Assigned to me (1)")
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionLabelAccentedPreview() {
    SuacTheme {
        SectionLabel(text = "Recently Used", color = AppTheme.colors.primary)
    }
}
