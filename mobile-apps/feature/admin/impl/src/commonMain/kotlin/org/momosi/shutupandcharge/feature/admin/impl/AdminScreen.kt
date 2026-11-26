package org.momosi.shutupandcharge.feature.admin.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.momosi.shutupandcharge.core.uilibrary.components.BottomSheet
import org.momosi.shutupandcharge.core.uilibrary.components.Button
import org.momosi.shutupandcharge.core.uilibrary.components.ButtonRole
import org.momosi.shutupandcharge.core.uilibrary.components.Chip
import org.momosi.shutupandcharge.core.uilibrary.components.ConfirmationDialog
import org.momosi.shutupandcharge.core.uilibrary.components.DashboardTopBar
import org.momosi.shutupandcharge.core.uilibrary.components.ErrorState
import org.momosi.shutupandcharge.core.uilibrary.components.FilterChipRow
import org.momosi.shutupandcharge.core.uilibrary.components.Icon
import org.momosi.shutupandcharge.core.uilibrary.components.InputDialog
import org.momosi.shutupandcharge.core.uilibrary.components.InputKind
import org.momosi.shutupandcharge.core.uilibrary.components.LoadingSpinner
import org.momosi.shutupandcharge.core.uilibrary.components.PullToRefresh
import org.momosi.shutupandcharge.core.uilibrary.components.Text
import org.momosi.shutupandcharge.core.uilibrary.components.TextButton
import org.momosi.shutupandcharge.core.uilibrary.components.amountToneOf
import org.momosi.shutupandcharge.core.uilibrary.components.color
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import org.momosi.shutupandcharge.feature.admin.impl.resources.Res
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_action_copy_id
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_action_disable_charging
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_action_enable_charging
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_action_rfid
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_action_top_up
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_cancel
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_charging_allowed
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_charging_blocked
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_chip_label
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_chip_none
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_chip_remove
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_chip_save
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_chip_title
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_load_failed
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_pending_empty
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_pending_meter
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_pending_sync
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_pending_sync_hint
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_retry
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_subtitle
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_tab_pending
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_tab_users
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_title
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_top_up_confirm
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_top_up_label
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_top_up_title
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_users_empty
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_verified_disable_message
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_verified_disable_title
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_verified_enable_message
import org.momosi.shutupandcharge.feature.admin.impl.resources.admin_verified_enable_title

private val AdminTabs = persistentListOf(AdminTab.Users, AdminTab.Pending)
private val TopUpPresets = listOf(10, 20, 50, 100)
private const val LOAD_MORE_THRESHOLD = 5

@Composable
internal fun AdminScreen(viewModel: AdminViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current

    AdminContent(
        state = state,
        onAction = viewModel::onAction,
        onCopy = { clipboard.setText(AnnotatedString(it)) },
    )
}

@Composable
private fun AdminContent(
    state: AdminState,
    onAction: (AdminAction) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabLabels = mapOf(
        AdminTab.Users to stringResource(Res.string.admin_tab_users),
        AdminTab.Pending to stringResource(Res.string.admin_tab_pending),
    )
    Column(modifier = modifier.fillMaxSize()) {
        DashboardTopBar(title = stringResource(Res.string.admin_title), subtitle = stringResource(Res.string.admin_subtitle))
        FilterChipRow(
            items = AdminTabs,
            selectedItem = state.tab,
            labelSelector = { tabLabels.getValue(it) },
            onSelect = { onAction(AdminAction.SelectTab(it)) },
            modifier = Modifier.background(AppTheme.colors.surfaceDim).padding(horizontal = 16.dp, vertical = 8.dp),
        )
        PullToRefresh(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(AdminAction.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            when (state.tab) {
                AdminTab.Users -> UsersTab(users = state.users, onAction = onAction)
                AdminTab.Pending -> PendingTab(pending = state.pending, isSyncing = state.isSyncing, onAction = onAction)
            }
        }
    }

    state.selectedUser?.let { user -> UserActionsSheet(user = user, onAction = onAction, onCopy = onCopy) }
    state.dialog?.let { dialog -> AdminDialogContent(dialog = dialog, onAction = onAction) }
}

@Composable
private fun UsersTab(users: UsersContent, onAction: (AdminAction) -> Unit) {
    when (users) {
        UsersContent.Loading -> LoadingSpinner(modifier = Modifier.fillMaxSize())

        is UsersContent.Failed -> ErrorState(
            reason = users.reason,
            message = stringResource(Res.string.admin_load_failed),
            onRetry = { onAction(AdminAction.RetryUsers) },
        )

        is UsersContent.Loaded -> {
            val listState = rememberLazyListState()
            LaunchedEffect(listState, users.canLoadMore) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
                    .distinctUntilChanged()
                    .filter { users.canLoadMore && it >= listState.layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD }
                    .collect { onAction(AdminAction.LoadMoreUsers) }
            }
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize().testTag("admin_users_list"), contentPadding = PaddingValues(vertical = 8.dp)) {
                if (users.items.isEmpty()) {
                    item { EmptyText(stringResource(Res.string.admin_users_empty)) }
                }
                items(users.items, key = { it.id }) { user ->
                    UserRow(user = user, onClick = { onAction(AdminAction.OpenUser(user.id)) })
                }
                if (users.isLoadingMore) {
                    item { LoadingSpinner(modifier = Modifier.fillMaxWidth().padding(16.dp), size = 32.dp) }
                }
            }
        }
    }
}

@Composable
private fun UserRow(user: AccountItem, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surfaceContainerLowest)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("admin_user_${user.id}"),
    ) {
        Icon(imageVector = SuacIcons.AccountCircle, tint = AppTheme.colors.primary, modifier = Modifier.size(36.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.name, style = AppTheme.typography.titleSmall, color = AppTheme.colors.onSurface)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    imageVector = if (user.verifiedForCharging) SuacIcons.Verified else SuacIcons.Offline,
                    tint = if (user.verifiedForCharging) AppTheme.colors.positive else AppTheme.colors.error,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = stringResource(if (user.verifiedForCharging) Res.string.admin_charging_allowed else Res.string.admin_charging_blocked),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
                if (user.chipUid != null) {
                    Icon(imageVector = SuacIcons.Rfid, tint = AppTheme.colors.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp).size(14.dp))
                }
            }
        }
        Text(
            text = user.balanceText,
            style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = amountToneOf(user.balance).color(),
        )
    }
}

@Composable
private fun PendingTab(
    pending: PendingContent,
    isSyncing: Boolean,
    onAction: (AdminAction) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize().testTag("admin_pending_list"), contentPadding = PaddingValues(vertical = 8.dp)) {
        item(key = "sync") {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(Res.string.admin_pending_sync_hint),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
                Button(
                    text = stringResource(Res.string.admin_pending_sync),
                    onClick = { onAction(AdminAction.SyncPending) },
                    loading = isSyncing,
                    icon = SuacIcons.Sync,
                    modifier = Modifier.fillMaxWidth().testTag("admin_sync_button"),
                )
            }
        }
        when (pending) {
            PendingContent.Loading -> item { LoadingSpinner(modifier = Modifier.fillMaxWidth().padding(24.dp)) }

            is PendingContent.Failed -> item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(Res.string.admin_load_failed),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.error,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(text = stringResource(Res.string.admin_retry), onClick = { onAction(AdminAction.RetryPending) })
                }
            }

            is PendingContent.Loaded -> {
                if (pending.items.isEmpty()) item { EmptyText(stringResource(Res.string.admin_pending_empty)) }
                items(pending.items, key = { it.id }) { PendingRow(it) }
            }
        }
    }
}

@Composable
private fun PendingRow(item: PendingItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surfaceContainerLowest)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Icon(imageVector = SuacIcons.Pending, tint = AppTheme.colors.warning, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "#${item.number} · ${item.accountName}", style = AppTheme.typography.titleSmall, color = AppTheme.colors.onSurface)
            Text(text = item.startedText, style = AppTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
        }
        Text(
            text = stringResource(Res.string.admin_pending_meter, item.energyMeter.toString()),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun UserActionsSheet(
    user: AccountItem,
    onAction: (AdminAction) -> Unit,
    onCopy: (String) -> Unit,
) {
    BottomSheet(onDismiss = { onAction(AdminAction.CloseUser) }) {
        Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(text = user.name, style = AppTheme.typography.titleLarge, color = AppTheme.colors.onSurface)
                Text(
                    text = user.balanceText,
                    style = AppTheme.typography.titleMedium,
                    color = amountToneOf(user.balance).color(),
                )
                Text(
                    text = user.chipUid?.let { stringResource(Res.string.admin_chip_label) + ": " + it } ?: stringResource(Res.string.admin_chip_none),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
            SheetAction(icon = SuacIcons.TopUp, text = stringResource(Res.string.admin_action_top_up), tag = "admin_action_top_up") {
                onAction(AdminAction.StartTopUp)
            }
            SheetAction(
                icon = if (user.verifiedForCharging) SuacIcons.Offline else SuacIcons.Verified,
                text = stringResource(if (user.verifiedForCharging) Res.string.admin_action_disable_charging else Res.string.admin_action_enable_charging),
                tag = "admin_action_toggle_verified",
            ) { onAction(AdminAction.StartToggleVerified) }
            SheetAction(icon = SuacIcons.Rfid, text = stringResource(Res.string.admin_action_rfid), tag = "admin_action_rfid") {
                onAction(AdminAction.StartEditChip)
            }
            SheetAction(icon = SuacIcons.Copy, text = stringResource(Res.string.admin_action_copy_id), tag = "admin_action_copy_id") {
                onCopy(user.keycloakId)
                onAction(AdminAction.CloseUser)
            }
        }
    }
}

@Composable
private fun SheetAction(
    icon: ImageVector,
    text: String,
    tag: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .testTag(tag),
    ) {
        Icon(imageVector = icon, tint = AppTheme.colors.onSurfaceVariant)
        Text(text = text, style = AppTheme.typography.bodyLarge, color = AppTheme.colors.onSurface)
    }
}

@Composable
private fun AdminDialogContent(dialog: AdminDialog, onAction: (AdminAction) -> Unit) {
    when (dialog) {
        is AdminDialog.TopUp -> InputDialog(
            title = stringResource(Res.string.admin_top_up_title),
            message = dialog.account.name,
            value = dialog.amountText,
            label = stringResource(Res.string.admin_top_up_label),
            confirmText = stringResource(Res.string.admin_top_up_confirm),
            dismissText = stringResource(Res.string.admin_cancel),
            inputKind = InputKind.Decimal,
            confirmEnabled = dialog.amount != null,
            isInProgress = dialog.isInProgress,
            onValueChange = { onAction(AdminAction.UpdateDialogInput(it)) },
            onConfirm = { onAction(AdminAction.ConfirmDialog) },
            onDismiss = { onAction(AdminAction.DismissDialog) },
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TopUpPresets.forEach { preset ->
                    Chip(
                        label = "$preset €",
                        selected = dialog.amount == preset.toDouble(),
                        onClick = { onAction(AdminAction.UpdateDialogInput(preset.toString())) },
                    )
                }
            }
        }

        is AdminDialog.ToggleVerified -> {
            val enable = !dialog.account.verifiedForCharging
            ConfirmationDialog(
                title = stringResource(if (enable) Res.string.admin_verified_enable_title else Res.string.admin_verified_disable_title),
                message = stringResource(
                    if (enable) Res.string.admin_verified_enable_message else Res.string.admin_verified_disable_message,
                    dialog.account.name,
                ),
                confirmText = stringResource(if (enable) Res.string.admin_action_enable_charging else Res.string.admin_action_disable_charging),
                dismissText = stringResource(Res.string.admin_cancel),
                confirmButtonRole = if (enable) ButtonRole.Positive else ButtonRole.Warning,
                onConfirm = { onAction(AdminAction.ConfirmDialog) },
                onDismiss = { onAction(AdminAction.DismissDialog) },
            )
        }

        is AdminDialog.EditChip -> InputDialog(
            title = stringResource(Res.string.admin_chip_title),
            message = dialog.account.name,
            value = dialog.chipUid,
            label = stringResource(Res.string.admin_chip_label),
            confirmText = stringResource(Res.string.admin_chip_save),
            dismissText = stringResource(Res.string.admin_cancel),
            confirmEnabled = dialog.chipUid.isNotBlank() && dialog.chipUid.trim() != dialog.account.chipUid,
            isInProgress = dialog.isInProgress,
            onValueChange = { onAction(AdminAction.UpdateDialogInput(it)) },
            onConfirm = { onAction(AdminAction.ConfirmDialog) },
            onDismiss = { onAction(AdminAction.DismissDialog) },
        ) {
            if (dialog.account.chipUid != null) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(text = stringResource(Res.string.admin_chip_remove), onClick = { onAction(AdminAction.ClearChip) })
                }
            }
        }
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(text = text, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.onSurfaceVariant, modifier = Modifier.padding(24.dp))
}

private val PreviewUsers = persistentListOf(
    AccountItem(1, "kc-1", "Martin Styk", "42,50 €", 42.5, verifiedForCharging = true, chipUid = "04A1B2C3"),
    AccountItem(2, "kc-2", "Jana Nováková", "-3,10 €", -3.1, verifiedForCharging = false, chipUid = null),
)

@Preview(heightDp = 640)
@Composable
private fun AdminUsersPreview() {
    SuacTheme {
        AdminContent(
            state = AdminState(users = UsersContent.Loaded(PreviewUsers, canLoadMore = false, isLoadingMore = false)),
            onAction = {},
            onCopy = {},
        )
    }
}

@Preview(heightDp = 640)
@Composable
private fun AdminPendingPreview() {
    SuacTheme(isDarkTheme = true) {
        AdminContent(
            state = AdminState(
                tab = AdminTab.Pending,
                pending = PendingContent.Loaded(persistentListOf(PendingItem(1, 42, "abc", "Martin Styk", "Nov 26, 2026, 10:12", 123456))),
            ),
            onAction = {},
            onCopy = {},
        )
    }
}
