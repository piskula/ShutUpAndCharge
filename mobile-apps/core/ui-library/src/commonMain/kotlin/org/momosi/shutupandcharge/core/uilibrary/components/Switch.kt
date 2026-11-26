package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedTrackColor = AppTheme.colors.primary,
            checkedThumbColor = AppTheme.colors.onPrimary,
            uncheckedTrackColor = AppTheme.colors.surfaceVariant,
            uncheckedThumbColor = AppTheme.colors.onSurfaceVariant,
        ),
    )
}

@Preview
@Composable
private fun SwitchOnPreview() {
    SuacTheme {
        Switch(checked = true, onCheckedChange = {})
    }
}

@Preview
@Composable
private fun SwitchOffPreview() {
    SuacTheme {
        Switch(checked = false, onCheckedChange = {})
    }
}
