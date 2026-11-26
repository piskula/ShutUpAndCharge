package org.momosi.shutupandcharge.feature.dashboard.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.momosi.shutupandcharge.core.charger.model.ChargerState
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.common.network.FailureReason
import org.momosi.shutupandcharge.core.transaction.display.TransactionDisplay
import org.momosi.shutupandcharge.core.uilibrary.charger.ChargerStatusCard
import org.momosi.shutupandcharge.core.uilibrary.components.DashboardTopBar
import org.momosi.shutupandcharge.core.uilibrary.components.Icon
import org.momosi.shutupandcharge.core.uilibrary.components.LoadingSpinner
import org.momosi.shutupandcharge.core.uilibrary.components.PullToRefresh
import org.momosi.shutupandcharge.core.uilibrary.components.SectionLabel
import org.momosi.shutupandcharge.core.uilibrary.components.SkeletonBox
import org.momosi.shutupandcharge.core.uilibrary.components.Text
import org.momosi.shutupandcharge.core.uilibrary.components.TextButton
import org.momosi.shutupandcharge.core.uilibrary.components.amountToneOf
import org.momosi.shutupandcharge.core.uilibrary.components.color
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.modifier.card
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import org.momosi.shutupandcharge.core.uilibrary.transaction.TransactionRow
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.Res
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_balance_label
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_greeting
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_last_chargings
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_last_chargings_empty
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_load_failed
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_monthly_chargings
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_monthly_empty
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_monthly_summary
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_monthly_top_ups
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_no_connection
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_retry
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_show_all
import org.momosi.shutupandcharge.feature.dashboard.impl.resources.dashboard_subtitle

@Composable
internal fun DashboardScreen(onShowAllTransactions: () -> Unit, viewModel: DashboardViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardContent(
        state = state,
        onAction = viewModel::onAction,
        onShowAllTransactions = onShowAllTransactions,
    )
}

@Composable
private fun DashboardContent(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    onShowAllTransactions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DashboardTopBar(
            title = stringResource(Res.string.dashboard_greeting, state.userName),
            subtitle = stringResource(Res.string.dashboard_subtitle),
        )
        PullToRefresh(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(DashboardAction.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag("dashboard_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "balance") {
                    BalanceCard(balance = state.balance, onRetry = { onAction(DashboardAction.RetryBalance) })
                }
                item(key = "charger") {
                    ChargerStatusCard(
                        status = state.charger.status,
                        chargedKwhText = state.charger.chargedKwhText,
                        occupiedSinceText = state.charger.occupiedSinceText,
                        isSignedIn = true,
                        isRefreshing = state.charger.isRefreshing,
                        isActionInProgress = state.charger.isActionInProgress,
                        isCurrentChangeInProgress = state.charger.isCurrentChangeInProgress,
                        onRefresh = { onAction(DashboardAction.RefreshCharger) },
                        onStart = { onAction(DashboardAction.StartCharging) },
                        onStop = { onAction(DashboardAction.StopCharging) },
                        onSelectCurrent = { onAction(DashboardAction.SelectCurrent(it)) },
                    )
                }
                item(key = "last_label") { SectionLabel(text = stringResource(Res.string.dashboard_last_chargings)) }
                section(
                    key = "last",
                    content = state.lastChargings,
                    emptyText = { stringResource(Res.string.dashboard_last_chargings_empty) },
                    onRetry = { onAction(DashboardAction.RetryLastChargings) },
                ) { item -> TransactionRow(transaction = item, showAccount = false) }
                item(key = "show_all") {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(text = stringResource(Res.string.dashboard_show_all), onClick = onShowAllTransactions)
                    }
                }
                item(key = "monthly_label") { SectionLabel(text = stringResource(Res.string.dashboard_monthly_summary)) }
                section(
                    key = "monthly",
                    content = state.monthlySummary,
                    emptyText = { stringResource(Res.string.dashboard_monthly_empty) },
                    onRetry = { onAction(DashboardAction.RetryMonthlySummary) },
                ) { item -> MonthlySummaryRow(item) }
            }
        }
    }
}

private fun <T> LazyListScope.section(
    key: String,
    content: SectionContent<T>,
    emptyText: @Composable () -> String,
    onRetry: () -> Unit,
    row: @Composable (T) -> Unit,
) {
    item(key = key) {
        Column(modifier = Modifier.fillMaxWidth().card()) {
            when (content) {
                SectionContent.Loading -> Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    LoadingSpinner(size = 32.dp)
                }

                is SectionContent.Failed -> InlineError(reason = content.reason, onRetry = onRetry)

                is SectionContent.Loaded -> if (content.items.isEmpty()) {
                    Text(
                        text = emptyText(),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                    )
                } else {
                    content.items.forEach { row(it) }
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: BalanceContent, onRetry: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().card().padding(16.dp).testTag("dashboard_balance"),
    ) {
        Icon(imageVector = SuacIcons.Wallet, tint = AppTheme.colors.primary, modifier = Modifier.size(32.dp))
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                text = stringResource(Res.string.dashboard_balance_label),
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.onSurfaceVariant,
            )
            when (balance) {
                BalanceContent.Loading -> SkeletonBox(modifier = Modifier.padding(top = 4.dp).size(width = 120.dp, height = 28.dp))

                is BalanceContent.Loaded -> Text(
                    text = balance.text,
                    style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = amountToneOf(balance.amount).color(),
                )

                is BalanceContent.Failed -> Text(
                    text = stringResource(Res.string.dashboard_load_failed),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.error,
                )
            }
        }
        if (balance is BalanceContent.Failed) {
            TextButton(text = stringResource(Res.string.dashboard_retry), onClick = onRetry)
        }
    }
}

@Composable
private fun MonthlySummaryRow(item: MonthlySummaryItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = item.monthText, style = AppTheme.typography.titleSmall, color = AppTheme.colors.onSurface)
            Text(
                text = stringResource(Res.string.dashboard_monthly_chargings, item.chargingCount.toInt(), item.chargingSumText),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
            if (item.topUpCount > 0) {
                Text(
                    text = stringResource(Res.string.dashboard_monthly_top_ups, item.topUpCount.toInt(), item.topUpSumText),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
        }
        Text(
            text = item.totalText,
            style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = amountToneOf(item.total).color(),
        )
    }
}

@Composable
private fun InlineError(reason: FailureReason, onRetry: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
    ) {
        Text(
            text = stringResource(if (reason == FailureReason.NoConnection) Res.string.dashboard_no_connection else Res.string.dashboard_load_failed),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.error,
            modifier = Modifier.weight(1f),
        )
        TextButton(text = stringResource(Res.string.dashboard_retry), onClick = onRetry)
    }
}

private val PreviewState = DashboardState(
    userName = "Martin Styk",
    balance = BalanceContent.Loaded(text = "42,50 €", amount = 42.5),
    charger = ChargerContent(
        status = ChargerStatus.Unknown.copy(state = ChargerState.CarConnected),
        chargedKwhText = "0,00 kWh",
        occupiedSinceText = null,
        isRefreshing = false,
        isActionInProgress = false,
        isCurrentChangeInProgress = false,
    ),
    lastChargings = SectionContent.Loaded(
        persistentListOf(
            TransactionDisplay(
                id = "1",
                dateText = "Nov 21, 2026, 18:42",
                stationId = "Kutlíkova",
                accountName = "Martin Styk",
                priceText = "-4,12 €",
                price = -4.12,
                kwhText = "13,42 kWh",
                isTopUp = false,
                triggeredByChip = false,
                link = null,
            ),
        ),
    ),
    monthlySummary = SectionContent.Loaded(
        persistentListOf(
            MonthlySummaryItem(
                monthText = "November 2026",
                chargingCount = 4,
                chargingSumText = "-16,20 €",
                topUpCount = 1,
                topUpSumText = "50,00 €",
                totalText = "33,80 €",
                total = 33.8,
            ),
        ),
    ),
    isRefreshing = false,
)

@Preview(heightDp = 1200)
@Composable
private fun DashboardContentPreview() {
    SuacTheme {
        DashboardContent(state = PreviewState, onAction = {}, onShowAllTransactions = {})
    }
}

@Preview(heightDp = 900)
@Composable
private fun DashboardContentLoadingPreview() {
    SuacTheme(isDarkTheme = true) {
        DashboardContent(
            state = PreviewState.copy(
                balance = BalanceContent.Loading,
                lastChargings = SectionContent.Failed(FailureReason.NoConnection),
                monthlySummary = SectionContent.Loading,
            ),
            onAction = {},
            onShowAllTransactions = {},
        )
    }
}
