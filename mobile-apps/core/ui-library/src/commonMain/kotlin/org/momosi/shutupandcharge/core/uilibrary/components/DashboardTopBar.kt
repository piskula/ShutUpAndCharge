package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import androidx.compose.material3.IconButton as MaterialIconButton

private val TopBarContentHeight = 52.dp

@Composable
fun DashboardTopBar(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surfaceDim)
            .statusBarsPadding()
            .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp)
            // Same height with or without action buttons, so titles line up across tabs.
            .height(TopBarContentHeight),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.onSurface,
            )
            Text(
                text = subtitle,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurface,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
}

@Composable
fun TopBarIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    MaterialIconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = AppTheme.colors.onSurface,
        ),
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Preview
@Composable
private fun DashboardTopBarPreview() {
    SuacTheme {
        DashboardTopBar(
            title = "Dashboard",
            subtitle = "Martin Styk · User",
            actions = {
                TopBarIconButton(icon = SuacIcons.Refresh, onClick = {})
                TopBarIconButton(icon = SuacIcons.AccountCircle, onClick = {})
            },
        )
    }
}

@Preview
@Composable
private fun DashboardTopBarAdminPreview() {
    SuacTheme {
        DashboardTopBar(
            title = "Administration",
            subtitle = "Martin Styk · Admin",
            actions = {
                TopBarIconButton(icon = SuacIcons.Refresh, onClick = {})
                TopBarIconButton(icon = SuacIcons.AccountCircle, onClick = {})
            },
        )
    }
}
