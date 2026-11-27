package org.momosi.shutupandcharge.feature.transactions.impl.monthly

import org.momosi.shutupandcharge.core.uilibrary.components.ChartMode

sealed interface MonthlyUsageAction {
    data object Retry : MonthlyUsageAction
    data class SelectMonth(val index: Int) : MonthlyUsageAction
    data class SelectMode(val mode: ChartMode) : MonthlyUsageAction
}
