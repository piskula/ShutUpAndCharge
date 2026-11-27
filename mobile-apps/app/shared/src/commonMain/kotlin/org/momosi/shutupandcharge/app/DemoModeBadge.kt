package org.momosi.shutupandcharge.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.momosi.shutupandcharge.app.resources.Res
import org.momosi.shutupandcharge.app.resources.demo_mode_badge
import org.momosi.shutupandcharge.core.uilibrary.components.Text
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

/** Marks builds running on in-memory demo data so they are never mistaken for production. */
@Composable
internal fun DemoModeBadge(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.demo_mode_badge),
        style = AppTheme.typography.labelSmall,
        color = AppTheme.colors.onWarning,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 2.dp)
            .background(AppTheme.colors.warning, RoundedCornerShape(percent = 50))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

@Preview
@Composable
private fun DemoModeBadgePreview() {
    SuacTheme {
        DemoModeBadge()
    }
}
