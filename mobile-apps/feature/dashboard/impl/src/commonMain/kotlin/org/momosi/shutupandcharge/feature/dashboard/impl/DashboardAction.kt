package org.momosi.shutupandcharge.feature.dashboard.impl

sealed interface DashboardAction {
    data object Refresh : DashboardAction
    data object RefreshCharger : DashboardAction
    data object StartCharging : DashboardAction
    data object StopCharging : DashboardAction
    data class SelectCurrent(val current: Int) : DashboardAction
    data object RetryBalance : DashboardAction
    data object RetryLastChargings : DashboardAction
    data object RetryMonthlySummary : DashboardAction
}
