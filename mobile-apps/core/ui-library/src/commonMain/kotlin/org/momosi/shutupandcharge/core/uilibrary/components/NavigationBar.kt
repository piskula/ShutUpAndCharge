package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Stable
data class NavigationBarItem(val navKey: NavKey, val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val title: String)

@Composable
fun NavigationBar(
    items: ImmutableList<NavigationBarItem>,
    selectedKey: NavKey,
    isVisible: Boolean,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppVarVisibility(
        isVisible = isVisible,
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surfaceContainer),
    ) {
        BottomAppBar(containerColor = AppTheme.colors.surfaceContainer) {
            items.forEach { item ->
                val isSelected = item.navKey == selectedKey
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelectKey(item.navKey) },
                    modifier = Modifier.testTag("nav_bar_item_${item.navKey::class.simpleName}"),
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                        )
                    },
                    label = {
                        Text(item.title)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppTheme.colors.onPrimaryContainer,
                        selectedTextColor = AppTheme.colors.onSurface,
                        indicatorColor = AppTheme.colors.primaryContainer,
                        unselectedIconColor = AppTheme.colors.onSurfaceVariant,
                        unselectedTextColor = AppTheme.colors.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Composable
private fun BottomAppVarVisibility(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = expandVertically(
            animationSpec = tween(
                durationMillis = 200,
                delayMillis = 100,
            ),
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 200,
                delayMillis = 100,
            ),
        ),
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = 200,
                delayMillis = 100,
            ),
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 200,
                delayMillis = 100,
            ),
        ),
    ) {
        content()
    }
}

private data object PreviewDashboardKey : NavKey

private data object PreviewTransactionsKey : NavKey

private data object PreviewProfileKey : NavKey

private val PreviewItems = persistentListOf(
    NavigationBarItem(PreviewDashboardKey, SuacIcons.Dashboard, SuacIcons.DashboardOutlined, "Dashboard"),
    NavigationBarItem(PreviewTransactionsKey, SuacIcons.Transactions, SuacIcons.Transactions, "Transactions"),
    NavigationBarItem(PreviewProfileKey, SuacIcons.AccountCircle, SuacIcons.AccountCircleOutlined, "Profile"),
)

@Preview
@Composable
private fun NavigationBarPreview() {
    SuacTheme {
        NavigationBar(items = PreviewItems, selectedKey = PreviewDashboardKey, isVisible = true, onSelectKey = {})
    }
}

@Preview
@Composable
private fun NavigationBarDarkPreview() {
    SuacTheme(isDarkTheme = true) {
        NavigationBar(items = PreviewItems, selectedKey = PreviewTransactionsKey, isVisible = true, onSelectKey = {})
    }
}
