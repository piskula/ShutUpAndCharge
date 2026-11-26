package org.momosi.shutupandcharge.feature.transactions.impl

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.momosi.shutupandcharge.core.common.network.FailureReason
import org.momosi.shutupandcharge.core.transaction.display.TransactionDisplay
import org.momosi.shutupandcharge.core.transaction.model.TransactionType
import org.momosi.shutupandcharge.core.uilibrary.components.ChartSeries
import org.momosi.shutupandcharge.core.uilibrary.components.DashboardTopBar
import org.momosi.shutupandcharge.core.uilibrary.components.ErrorState
import org.momosi.shutupandcharge.core.uilibrary.components.FilterChipRow
import org.momosi.shutupandcharge.core.uilibrary.components.LoadingSpinner
import org.momosi.shutupandcharge.core.uilibrary.components.PullToRefresh
import org.momosi.shutupandcharge.core.uilibrary.components.StackedBarChart
import org.momosi.shutupandcharge.core.uilibrary.components.Switch
import org.momosi.shutupandcharge.core.uilibrary.components.Text
import org.momosi.shutupandcharge.core.uilibrary.components.TextButton
import org.momosi.shutupandcharge.core.uilibrary.components.TopBarIconButton
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.modifier.card
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import org.momosi.shutupandcharge.core.uilibrary.transaction.TransactionRow
import org.momosi.shutupandcharge.feature.transactions.impl.resources.Res
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_chart_failed
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_chart_title
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_empty
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_load_failed
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_load_more_failed
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_only_mine
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_retry
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_subtitle_admin
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_subtitle_user
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_title
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_type_all
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_type_charging
import org.momosi.shutupandcharge.feature.transactions.impl.resources.transactions_type_top_ups

private const val LOAD_MORE_THRESHOLD = 5
private val TransactionTypes = persistentListOf(TransactionType.All, TransactionType.TopUps, TransactionType.Charging)

@Composable
internal fun TransactionsScreen(viewModel: TransactionsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TransactionsContent(state = state, onAction = viewModel::onAction)
}

@Composable
private fun TransactionsContent(
    state: TransactionsState,
    onAction: (TransactionsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DashboardTopBar(
            title = stringResource(Res.string.transactions_title),
            subtitle = stringResource(if (state.isAdmin) Res.string.transactions_subtitle_admin else Res.string.transactions_subtitle_user),
            actions = {
                if (state.isExporting) {
                    LoadingSpinner(modifier = Modifier.padding(12.dp), size = 24.dp, strokeWidth = 2.dp)
                } else {
                    TopBarIconButton(
                        icon = SuacIcons.Share,
                        onClick = { onAction(TransactionsAction.Export) },
                        modifier = Modifier.testTag("transactions_export_button"),
                    )
                }
            },
        )
        if (state.isAdmin) {
            Filters(filter = state.filter, onAction = onAction)
        }
        when (val list = state.list) {
            ListContent.Loading -> LoadingSpinner(modifier = Modifier.fillMaxSize())

            is ListContent.Failed -> ErrorState(
                reason = list.reason,
                message = stringResource(Res.string.transactions_load_failed),
                onRetry = { onAction(TransactionsAction.Retry) },
            )

            is ListContent.Loaded -> PullToRefresh(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(TransactionsAction.Refresh) },
                modifier = Modifier.fillMaxSize(),
            ) {
                TransactionList(list = list, chart = state.chart, showAccount = state.isAdmin, onAction = onAction)
            }
        }
    }
}

@Composable
private fun Filters(filter: FilterState, onAction: (TransactionsAction) -> Unit) {
    val labels = mapOf(
        TransactionType.All to stringResource(Res.string.transactions_type_all),
        TransactionType.TopUps to stringResource(Res.string.transactions_type_top_ups),
        TransactionType.Charging to stringResource(Res.string.transactions_type_charging),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surfaceDim)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.transactions_only_mine),
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = filter.onlyMine,
                onCheckedChange = { onAction(TransactionsAction.SetOnlyMine(it)) },
                modifier = Modifier.testTag("transactions_only_mine_switch"),
            )
        }
        FilterChipRow(
            items = TransactionTypes,
            selectedItem = filter.type,
            labelSelector = { labels.getValue(it) },
            onSelect = { onAction(TransactionsAction.SelectType(it)) },
        )
    }
}

@Composable
private fun TransactionList(
    list: ListContent.Loaded,
    chart: ChartContent,
    showAccount: Boolean,
    onAction: (TransactionsAction) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, list.canLoadMore) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .distinctUntilChanged()
            .filter { lastVisible -> list.canLoadMore && lastVisible >= listState.layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD }
            .collect { onAction(TransactionsAction.LoadMore) }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().testTag("transactions_list"),
        contentPadding = PaddingValues(vertical = 12.dp),
    ) {
        if (chart !is ChartContent.Hidden) {
            item(key = "chart") { ChartCard(chart = chart, onRetry = { onAction(TransactionsAction.RetryChart) }) }
        }
        if (list.items.isEmpty()) {
            item(key = "empty") {
                Text(
                    text = stringResource(Res.string.transactions_empty),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp),
                )
            }
        }
        items(list.items, key = { it.id }) { transaction ->
            TransactionRow(
                transaction = transaction,
                showAccount = showAccount,
                modifier = Modifier.background(AppTheme.colors.surfaceContainerLowest),
            )
        }
        item(key = "footer") {
            when (list.loadMore) {
                LoadMoreStatus.Loading -> LoadingSpinner(modifier = Modifier.fillMaxWidth().padding(16.dp), size = 32.dp)

                LoadMoreStatus.Failed -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.transactions_load_more_failed),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.error,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(text = stringResource(Res.string.transactions_retry), onClick = { onAction(TransactionsAction.LoadMore) })
                }

                LoadMoreStatus.Idle -> Box(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun ChartCard(chart: ChartContent, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .card()
            .padding(16.dp)
            .testTag("transactions_chart"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = stringResource(Res.string.transactions_chart_title), style = AppTheme.typography.titleMedium, color = AppTheme.colors.onSurface)
        when (chart) {
            ChartContent.Hidden -> Unit

            ChartContent.Loading -> LoadingSpinner(modifier = Modifier.fillMaxWidth().padding(24.dp), size = 32.dp)

            ChartContent.Failed -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.transactions_chart_failed),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.error,
                    modifier = Modifier.weight(1f),
                )
                TextButton(text = stringResource(Res.string.transactions_retry), onClick = onRetry)
            }

            is ChartContent.Loaded -> StackedBarChart(labels = chart.labels, series = chart.series, maxValueText = chart.maxValueText)
        }
    }
}

private val PreviewItems = persistentListOf(
    TransactionDisplay(
        id = "1",
        dateText = "Nov 21, 2026, 18:42",
        stationId = "Kutlíkova",
        accountName = "Martin Styk",
        priceText = "-4,12 €",
        price = -4.12,
        kwhText = "13,42 kWh",
        isTopUp = false,
        triggeredByChip = true,
        link = "https://example.com",
    ),
    TransactionDisplay(
        id = "2",
        dateText = "Nov 20, 2026, 09:10",
        stationId = null,
        accountName = "Martin Styk",
        priceText = "50,00 €",
        price = 50.0,
        kwhText = null,
        isTopUp = true,
        triggeredByChip = false,
        link = null,
    ),
)

@Preview(heightDp = 900)
@Composable
private fun TransactionsAdminPreview() {
    SuacTheme {
        TransactionsContent(
            state = TransactionsState(
                isAdmin = true,
                list = ListContent.Loaded(PreviewItems, canLoadMore = true, loadMore = LoadMoreStatus.Loading),
                chart = ChartContent.Loaded(
                    labels = persistentListOf("9/26", "10/26", "11/26"),
                    series = persistentListOf(
                        ChartSeries("Martin", false, persistentListOf(40.0, 55.0, 21.0)),
                        ChartSeries("Other", true, persistentListOf(10.0, 12.0, 8.0)),
                    ),
                    maxValueText = "67,00 kWh",
                ),
            ),
            onAction = {},
        )
    }
}

@Preview(heightDp = 640)
@Composable
private fun TransactionsUserPreview() {
    SuacTheme(isDarkTheme = true) {
        TransactionsContent(
            state = TransactionsState(list = ListContent.Loaded(PreviewItems, canLoadMore = false, loadMore = LoadMoreStatus.Idle)),
            onAction = {},
        )
    }
}

@Preview(heightDp = 640)
@Composable
private fun TransactionsErrorPreview() {
    SuacTheme {
        TransactionsContent(state = TransactionsState(list = ListContent.Failed(FailureReason.NoConnection)), onAction = {})
    }
}
