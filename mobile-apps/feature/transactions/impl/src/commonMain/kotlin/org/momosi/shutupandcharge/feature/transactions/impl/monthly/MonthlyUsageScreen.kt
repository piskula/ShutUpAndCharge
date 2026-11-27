package org.momosi.shutupandcharge.feature.transactions.impl.monthly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.momosi.shutupandcharge.core.common.network.FailureReason
import org.momosi.shutupandcharge.core.uilibrary.components.ChartMode
import org.momosi.shutupandcharge.core.uilibrary.components.ChartSeries
import org.momosi.shutupandcharge.core.uilibrary.components.ErrorState
import org.momosi.shutupandcharge.core.uilibrary.components.FilterChipRow
import org.momosi.shutupandcharge.core.uilibrary.components.LoadingSpinner
import org.momosi.shutupandcharge.core.uilibrary.components.SectionLabel
import org.momosi.shutupandcharge.core.uilibrary.components.StackedBarChart
import org.momosi.shutupandcharge.core.uilibrary.components.Text
import org.momosi.shutupandcharge.core.uilibrary.components.Toolbar
import org.momosi.shutupandcharge.core.uilibrary.components.chartSeriesColors
import org.momosi.shutupandcharge.core.uilibrary.modifier.card
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import org.momosi.shutupandcharge.feature.transactions.impl.resources.Res
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_breakdown
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_energy
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_hint
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_load_failed
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_mode_by_user
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_mode_total
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_other
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_period_total
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_range
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_scope_all
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_scope_mine
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_share
import org.momosi.shutupandcharge.feature.transactions.impl.resources.monthly_usage_title

private val ChartModes = persistentListOf(ChartMode.Total, ChartMode.BySeries)

@Composable
internal fun MonthlyUsageScreen(
    onlyMine: Boolean,
    onBack: () -> Unit,
    viewModel: MonthlyUsageViewModel = koinViewModel(parameters = { parametersOf(onlyMine) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MonthlyUsageContent(state = state, onAction = viewModel::onAction, onBack = onBack)
}

@Composable
private fun MonthlyUsageContent(
    state: MonthlyUsageState,
    onAction: (MonthlyUsageAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Toolbar(title = stringResource(Res.string.monthly_usage_title), onBack = onBack)
        when (state) {
            MonthlyUsageState.Loading -> LoadingSpinner(modifier = Modifier.fillMaxSize())

            is MonthlyUsageState.Failed -> ErrorState(
                reason = state.reason,
                message = stringResource(Res.string.monthly_usage_load_failed),
                onRetry = { onAction(MonthlyUsageAction.Retry) },
            )

            is MonthlyUsageState.Content -> LoadedContent(state = state, onAction = onAction)
        }
    }
}

@Composable
private fun LoadedContent(state: MonthlyUsageState.Content, onAction: (MonthlyUsageAction) -> Unit) {
    val colors = chartSeriesColors(state.series)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("monthly_usage_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(
                text = stringResource(if (state.isOnlyMine) Res.string.monthly_usage_scope_mine else Res.string.monthly_usage_scope_all),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(Res.string.monthly_usage_range, state.firstMonthText, state.lastMonthText),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
            )
        }
        ChartCard(state = state, onAction = onAction)
        if (state.breakdown.isNotEmpty()) {
            SectionLabel(text = stringResource(Res.string.monthly_usage_breakdown, state.selectedMonthTitle))
            Column(modifier = Modifier.fillMaxWidth().card().testTag("monthly_usage_breakdown")) {
                state.breakdown.forEach { row -> BreakdownItem(row = row, color = colors[row.seriesIndex]) }
            }
        }
        Text(
            text = stringResource(Res.string.monthly_usage_hint),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

@Composable
private fun ChartCard(state: MonthlyUsageState.Content, onAction: (MonthlyUsageAction) -> Unit) {
    val modeLabels = mapOf(
        ChartMode.Total to stringResource(Res.string.monthly_usage_mode_total),
        ChartMode.BySeries to stringResource(Res.string.monthly_usage_mode_by_user),
    )
    Column(
        modifier = Modifier.fillMaxWidth().card().padding(16.dp).testTag("monthly_usage_chart"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = stringResource(Res.string.monthly_usage_energy), style = AppTheme.typography.titleMedium, color = AppTheme.colors.onSurface)
        FilterChipRow(
            items = ChartModes,
            selectedItem = state.mode,
            labelSelector = { modeLabels.getValue(it) },
            onSelect = { onAction(MonthlyUsageAction.SelectMode(it)) },
            containerColor = Color.Transparent,
            contentPadding = PaddingValues(0.dp),
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = state.selectedTotalText,
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(text = state.selectedMonthTitle, style = AppTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
        }
        StackedBarChart(
            labels = state.monthLabels,
            descriptions = state.monthTitles,
            totalLabels = state.barLabels,
            series = state.series,
            selectedIndex = state.selectedIndex,
            onSelect = { onAction(MonthlyUsageAction.SelectMonth(it)) },
            mode = state.mode,
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppTheme.colors.outlineVariant))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.monthly_usage_period_total),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(text = state.periodTotalText, style = AppTheme.typography.titleSmall, color = AppTheme.colors.onSurface)
        }
    }
}

@Composable
private fun BreakdownItem(row: BreakdownRow, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Box(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(3.dp)))
        Text(
            text = if (row.isOther) stringResource(Res.string.monthly_usage_other) else row.name,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurface,
            maxLines = 1,
            modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp),
        )
        Text(text = row.kwhText, style = AppTheme.typography.titleSmall, color = AppTheme.colors.onSurface)
        Text(
            text = stringResource(Res.string.monthly_usage_share, row.sharePercent),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(48.dp),
        )
    }
}

private val PreviewContent = MonthlyUsageState.Content(
    isOnlyMine = false,
    firstMonthText = "June 2026",
    lastMonthText = "November 2026",
    monthLabels = persistentListOf("Jun", "Jul", "Aug", "Sep", "Oct", "Nov"),
    monthTitles = persistentListOf("June 2026", "July 2026", "August 2026", "September 2026", "October 2026", "November 2026"),
    barLabels = persistentListOf("65", "72", "56", "88", "79", "98"),
    series = persistentListOf(
        ChartSeries("Martin Styk", false, persistentListOf(40.0, 52.0, 31.0, 60.0, 45.0, 70.0)),
        ChartSeries("Jana Kováčová", false, persistentListOf(20.0, 12.0, 25.0, 18.0, 30.0, 22.0)),
        ChartSeries("Other", true, persistentListOf(5.0, 8.0, 0.0, 10.0, 4.0, 6.0)),
    ),
    mode = ChartMode.BySeries,
    selectedIndex = 5,
    selectedTotalText = "98.00 kWh",
    periodTotalText = "458.00 kWh",
    breakdown = persistentListOf(
        BreakdownRow(0, "Martin Styk", false, "70.00 kWh", 71),
        BreakdownRow(1, "Jana Kováčová", false, "22.00 kWh", 22),
        BreakdownRow(2, "Other", true, "6.00 kWh", 6),
    ),
)

@Preview(heightDp = 1000)
@Composable
private fun MonthlyUsageContentPreview() {
    SuacTheme(isDarkTheme = true) {
        MonthlyUsageContent(state = PreviewContent, onAction = {}, onBack = {})
    }
}

@Preview(heightDp = 1000)
@Composable
private fun MonthlyUsageContentTotalPreview() {
    SuacTheme {
        MonthlyUsageContent(state = PreviewContent.copy(mode = ChartMode.Total, isOnlyMine = true), onAction = {}, onBack = {})
    }
}

@Preview(heightDp = 640)
@Composable
private fun MonthlyUsageContentFailedPreview() {
    SuacTheme {
        MonthlyUsageContent(state = MonthlyUsageState.Failed(FailureReason.NoConnection), onAction = {}, onBack = {})
    }
}
