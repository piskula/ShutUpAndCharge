package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = CheckboxDefaults.colors(
            checkedColor = AppTheme.colors.primary,
            uncheckedColor = AppTheme.colors.onSurfaceVariant,
            checkmarkColor = AppTheme.colors.onPrimary,
        ),
    )
}

@Preview
@Composable
private fun CheckboxCheckedPreview() {
    SuacTheme {
        Checkbox(checked = true, onCheckedChange = {})
    }
}

@Preview
@Composable
private fun CheckboxUncheckedPreview() {
    SuacTheme {
        Checkbox(checked = false, onCheckedChange = {})
    }
}
