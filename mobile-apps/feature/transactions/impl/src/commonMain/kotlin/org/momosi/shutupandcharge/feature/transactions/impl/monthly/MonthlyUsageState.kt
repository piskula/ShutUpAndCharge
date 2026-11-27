package org.momosi.shutupandcharge.feature.transactions.impl.monthly

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import org.momosi.shutupandcharge.core.common.network.FailureReason
import org.momosi.shutupandcharge.core.uilibrary.components.ChartMode
import org.momosi.shutupandcharge.core.uilibrary.components.ChartSeries

@Immutable
sealed interface MonthlyUsageState {
    data object Loading : MonthlyUsageState

    data class Failed(val reason: FailureReason) : MonthlyUsageState

    data class Content(
        val isOnlyMine: Boolean,
        val firstMonthText: String,
        val lastMonthText: String,
        val monthLabels: ImmutableList<String>,
        val monthTitles: ImmutableList<String>,
        val barLabels: ImmutableList<String>,
        val series: ImmutableList<ChartSeries>,
        val mode: ChartMode,
        val selectedIndex: Int,
        val selectedTotalText: String,
        val periodTotalText: String,
        val breakdown: ImmutableList<BreakdownRow>,
    ) : MonthlyUsageState {
        val selectedMonthTitle: String get() = monthTitles.getOrElse(selectedIndex) { "" }
    }
}

@Immutable
data class BreakdownRow(val seriesIndex: Int, val name: String, val isOther: Boolean, val kwhText: String, val sharePercent: Int)
