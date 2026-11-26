package org.momosi.shutupandcharge.feature.dashboard.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.momosi.shutupandcharge.core.account.AccountRepository
import org.momosi.shutupandcharge.core.charger.ChargerRepository
import org.momosi.shutupandcharge.core.charger.model.ChargerStatus
import org.momosi.shutupandcharge.core.common.coroutines.combine
import org.momosi.shutupandcharge.core.common.error.ErrorReporter
import org.momosi.shutupandcharge.core.common.formatter.DateFormatter
import org.momosi.shutupandcharge.core.common.formatter.NumberFormatter
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.toFailureReason
import org.momosi.shutupandcharge.core.transaction.TransactionRepository
import org.momosi.shutupandcharge.core.transaction.display.TransactionDisplay
import org.momosi.shutupandcharge.core.transaction.display.toDisplay
import org.momosi.shutupandcharge.core.transaction.model.MonthlySummary
import org.momosi.shutupandcharge.user.UserRepository

private const val LAST_CHARGINGS_SIZE = 5
private const val MONTHLY_SUMMARY_SIZE = 6

class DashboardViewModel(
    userRepository: UserRepository,
    private val chargerRepository: ChargerRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val numberFormatter: NumberFormatter,
    private val dateFormatter: DateFormatter,
    private val errorReporter: ErrorReporter,
) : ViewModel() {

    private val balance = MutableStateFlow<BalanceContent>(BalanceContent.Loading)
    private val lastChargings = MutableStateFlow<SectionContent<TransactionDisplay>>(SectionContent.Loading)
    private val monthlySummary = MutableStateFlow<SectionContent<MonthlySummaryItem>>(SectionContent.Loading)
    private val isRefreshing = MutableStateFlow(false)
    private val isRefreshingCharger = MutableStateFlow(false)
    private val isChargerActionInProgress = MutableStateFlow(false)
    private val pendingCurrent = MutableStateFlow<Int?>(null)

    private val charger: Flow<ChargerContent> = combine(
        chargerRepository.status.onStart { emit(ChargerStatus.Unknown) },
        isRefreshingCharger,
        isChargerActionInProgress,
        pendingCurrent,
    ) { status, refreshing, actionInProgress, current ->
        ChargerContent(
            status = current?.let { status.copy(current = it) } ?: status,
            chargedKwhText = numberFormatter.formatKwh(status.chargedKwh),
            occupiedSinceText = status.occupiedFrom?.let(dateFormatter::formatDateTime),
            isRefreshing = refreshing,
            isActionInProgress = actionInProgress,
            isCurrentChangeInProgress = current != null,
        )
    }

    private val initialState = DashboardState(
        userName = userRepository.user.value?.name.orEmpty(),
        balance = BalanceContent.Loading,
        charger = ChargerContent(
            status = ChargerStatus.Unknown,
            chargedKwhText = numberFormatter.formatKwh(0.0),
            occupiedSinceText = null,
            isRefreshing = false,
            isActionInProgress = false,
            isCurrentChangeInProgress = false,
        ),
        lastChargings = SectionContent.Loading,
        monthlySummary = SectionContent.Loading,
        isRefreshing = false,
    )

    val state: StateFlow<DashboardState> = combine(
        userRepository.user.map { it?.name.orEmpty() },
        balance,
        charger,
        lastChargings,
        monthlySummary,
        isRefreshing,
    ) { userName, balanceContent, chargerContent, last, monthly, refreshing ->
        DashboardState(
            userName = userName,
            balance = balanceContent,
            charger = chargerContent,
            lastChargings = last,
            monthlySummary = monthly,
            isRefreshing = refreshing,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = initialState,
    )

    init {
        viewModelScope.launch { loadAll() }
    }

    fun onAction(action: DashboardAction) {
        Logger.d("Action:Dashboard", action.toString())
        when (action) {
            DashboardAction.Refresh -> refresh()
            DashboardAction.RefreshCharger -> refreshCharger()
            DashboardAction.StartCharging -> runChargerAction { chargerRepository.startCharging() }
            DashboardAction.StopCharging -> runChargerAction { chargerRepository.stopCharging() }
            is DashboardAction.SelectCurrent -> selectCurrent(action.current)
            DashboardAction.RetryBalance -> viewModelScope.launch { loadBalance() }
            DashboardAction.RetryLastChargings -> viewModelScope.launch { loadLastChargings() }
            DashboardAction.RetryMonthlySummary -> viewModelScope.launch { loadMonthlySummary() }
        }
    }

    private fun refresh() {
        if (isRefreshing.value) return
        viewModelScope.launch {
            isRefreshing.value = true
            coroutineScope {
                launch { chargerRepository.refresh() }
                launch { loadAll() }
            }
            isRefreshing.value = false
        }
    }

    private suspend fun loadAll() = coroutineScope {
        launch { loadBalance() }
        launch { loadLastChargings() }
        launch { loadMonthlySummary() }
    }

    private suspend fun loadBalance() {
        if (balance.value !is BalanceContent.Loaded) balance.value = BalanceContent.Loading
        balance.value = accountRepository.getMyBalance().fold(
            onSuccess = { BalanceContent.Loaded(text = numberFormatter.formatEur(it), amount = it) },
            onFailure = { BalanceContent.Failed(it.toFailureReason()) },
        )
    }

    private suspend fun loadLastChargings() {
        if (lastChargings.value !is SectionContent.Loaded) lastChargings.value = SectionContent.Loading
        lastChargings.value = transactionRepository.getLastChargings(page = 0, size = LAST_CHARGINGS_SIZE).fold(
            onSuccess = { page -> SectionContent.Loaded(page.content.map { it.toDisplay(dateFormatter, numberFormatter) }.toImmutableList()) },
            onFailure = { SectionContent.Failed(it.toFailureReason()) },
        )
    }

    private suspend fun loadMonthlySummary() {
        if (monthlySummary.value !is SectionContent.Loaded) monthlySummary.value = SectionContent.Loading
        monthlySummary.value = transactionRepository.getMonthlySummary(page = 0, size = MONTHLY_SUMMARY_SIZE).fold(
            onSuccess = { page -> SectionContent.Loaded(page.content.map { it.toItem() }.toImmutableList()) },
            onFailure = { SectionContent.Failed(it.toFailureReason()) },
        )
    }

    private fun refreshCharger() {
        if (isRefreshingCharger.value) return
        viewModelScope.launch {
            isRefreshingCharger.value = true
            chargerRepository.refresh().onFailure { errorReporter.report(it) }
            isRefreshingCharger.value = false
        }
    }

    private fun runChargerAction(action: suspend () -> Result<ChargerStatus>) {
        if (isChargerActionInProgress.value) return
        viewModelScope.launch {
            isChargerActionInProgress.value = true
            action()
                .onSuccess { loadAll() }
                .onFailure { errorReporter.report(it) }
            isChargerActionInProgress.value = false
        }
    }

    private fun selectCurrent(current: Int) {
        if (pendingCurrent.value != null) return
        viewModelScope.launch {
            pendingCurrent.value = current
            chargerRepository.setCurrent(current)
                .onSuccess { chargerRepository.refresh() }
                .onFailure { errorReporter.report(it) }
            pendingCurrent.value = null
        }
    }

    private fun MonthlySummary.toItem() = MonthlySummaryItem(
        monthText = monthStart?.let(dateFormatter::formatMonthYear).orEmpty(),
        chargingCount = chargingCount,
        chargingSumText = numberFormatter.formatEur(chargingSum),
        topUpCount = topUpCount,
        topUpSumText = numberFormatter.formatEur(topUpSum),
        totalText = numberFormatter.formatEur(total),
        total = total,
    )
}
