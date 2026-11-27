package org.momosi.shutupandcharge.feature.transactions.impl.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.momosi.shutupandcharge.core.common.formatter.DateFormatter
import org.momosi.shutupandcharge.core.common.formatter.NumberFormatter
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.FailureReason
import org.momosi.shutupandcharge.core.common.network.toFailureReason
import org.momosi.shutupandcharge.core.transaction.TransactionRepository
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhChart
import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter
import org.momosi.shutupandcharge.core.uilibrary.components.ChartMode
import org.momosi.shutupandcharge.core.uilibrary.components.ChartSeries
import org.momosi.shutupandcharge.user.UserRepository

class MonthlyUsageViewModel(
    private val onlyMine: Boolean,
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val numberFormatter: NumberFormatter,
    private val dateFormatter: DateFormatter,
) : ViewModel() {

    private val chart = MutableStateFlow<ChartLoad>(ChartLoad.Loading)
    private val selectedMonth = MutableStateFlow<Int?>(null)
    private val mode = MutableStateFlow(ChartMode.BySeries)
    private var loadJob: Job? = null

    val state: StateFlow<MonthlyUsageState> = combine(chart, selectedMonth, mode) { load, selected, chartMode ->
        when (load) {
            ChartLoad.Loading -> MonthlyUsageState.Loading
            is ChartLoad.Failed -> MonthlyUsageState.Failed(load.reason)
            is ChartLoad.Loaded -> load.chart.toContent(selected, chartMode)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonthlyUsageState.Loading)

    init {
        load()
    }

    fun onAction(action: MonthlyUsageAction) {
        Logger.d("Action:MonthlyUsage", action.toString())
        when (action) {
            MonthlyUsageAction.Retry -> load()
            is MonthlyUsageAction.SelectMonth -> selectedMonth.value = action.index
            is MonthlyUsageAction.SelectMode -> mode.value = action.mode
        }
    }

    private fun load() {
        loadJob?.cancel()
        chart.value = ChartLoad.Loading
        val filter = TransactionFilter(onlyAccountKeycloakId = userRepository.user.value?.id?.takeIf { onlyMine })
        loadJob = viewModelScope.launch {
            chart.value = transactionRepository.getMonthlyKwh(filter).fold(
                onSuccess = { ChartLoad.Loaded(it) },
                onFailure = { ChartLoad.Failed(it.toFailureReason()) },
            )
        }
    }

    private fun MonthlyKwhChart.toContent(selected: Int?, chartMode: ChartMode): MonthlyUsageState.Content {
        val totals = monthTotals()
        val selectedIndex = selected?.takeIf { it in months.indices } ?: months.lastIndex
        val midpoints = months.map(::monthMidpoint)
        val titles = months.mapIndexed { index, month -> midpoints[index]?.let(dateFormatter::formatMonthYear) ?: month }
        return MonthlyUsageState.Content(
            isOnlyMine = onlyMine,
            firstMonthText = titles.firstOrNull().orEmpty(),
            lastMonthText = titles.lastOrNull().orEmpty(),
            monthLabels = months.mapIndexed { index, month -> midpoints[index]?.let(dateFormatter::formatShortMonth) ?: month }.toImmutableList(),
            monthTitles = titles.toImmutableList(),
            barLabels = totals.map { numberFormatter.formatDecimal(it, 0) }.toImmutableList(),
            series = series.map { ChartSeries(name = it.name, isOther = it.isOther, values = it.kwh.toImmutableList()) }.toImmutableList(),
            mode = chartMode,
            selectedIndex = selectedIndex,
            selectedTotalText = numberFormatter.formatKwh(totals.getOrElse(selectedIndex) { 0.0 }),
            periodTotalText = numberFormatter.formatKwh(totals.sum()),
            breakdown = sharesOf(selectedIndex).map { share ->
                val s = series[share.seriesIndex]
                BreakdownRow(
                    seriesIndex = share.seriesIndex,
                    name = s.name,
                    isOther = s.isOther,
                    kwhText = numberFormatter.formatKwh(share.kwh),
                    sharePercent = share.percent,
                )
            }.toImmutableList(),
        )
    }

    private sealed interface ChartLoad {
        data object Loading : ChartLoad
        data class Failed(val reason: FailureReason) : ChartLoad
        data class Loaded(val chart: MonthlyKwhChart) : ChartLoad
    }
}
