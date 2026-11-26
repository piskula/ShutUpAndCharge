package org.momosi.shutupandcharge.feature.dashboard.impl

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.common.network.FailureReason
import org.momosi.shutupandcharge.core.transaction.display.TransactionDisplay

@Immutable
data class DashboardState(
    val userName: String,
    val balance: BalanceContent,
    val charger: ChargerContent,
    val lastChargings: SectionContent<TransactionDisplay>,
    val monthlySummary: SectionContent<MonthlySummaryItem>,
    val isRefreshing: Boolean,
)

@Immutable
sealed interface BalanceContent {
    data object Loading : BalanceContent
    data class Loaded(val text: String, val amount: Double) : BalanceContent
    data class Failed(val reason: FailureReason) : BalanceContent
}

@Immutable
data class ChargerContent(val status: ChargerStatus, val chargedKwhText: String, val occupiedSinceText: String?, val isRefreshing: Boolean, val isActionInProgress: Boolean, val isCurrentChangeInProgress: Boolean)

@Immutable
sealed interface SectionContent<out T> {
    data object Loading : SectionContent<Nothing>
    data class Loaded<T>(val items: ImmutableList<T>) : SectionContent<T>
    data class Failed(val reason: FailureReason) : SectionContent<Nothing>
}

@Immutable
data class MonthlySummaryItem(val monthText: String, val chargingCount: Long, val chargingSumText: String, val topUpCount: Long, val topUpSumText: String, val totalText: String, val total: Double)
