package org.momosi.shutupandcharge.feature.transactions.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.momosi.shutupandcharge.core.common.coroutines.combine
import org.momosi.shutupandcharge.core.common.error.ErrorReporter
import org.momosi.shutupandcharge.core.common.formatter.DateFormatter
import org.momosi.shutupandcharge.core.common.formatter.NumberFormatter
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.toFailureReason
import org.momosi.shutupandcharge.core.transaction.TransactionRepository
import org.momosi.shutupandcharge.core.transaction.display.toDisplay
import org.momosi.shutupandcharge.core.transaction.export.TransactionExportManager
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhChart
import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter
import org.momosi.shutupandcharge.core.transaction.model.TransactionType
import org.momosi.shutupandcharge.core.uilibrary.components.ChartSeries
import org.momosi.shutupandcharge.user.UserRepository

private const val PAGE_SIZE = 20

class TransactionsViewModel(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val exportManager: TransactionExportManager,
    private val numberFormatter: NumberFormatter,
    private val dateFormatter: DateFormatter,
    private val errorReporter: ErrorReporter,
) : ViewModel() {

    private val filter = MutableStateFlow(FilterState())
    private val list = MutableStateFlow<ListContent>(ListContent.Loading)
    private val isRefreshing = MutableStateFlow(false)
    private val isExporting = MutableStateFlow(false)
    private val chart = MutableStateFlow<ChartContent>(ChartContent.Hidden)

    private var nextPage = 0
    private var listJob: Job? = null
    private var chartJob: Job? = null

    private val isAdmin: Boolean get() = userRepository.user.value?.isAdmin == true

    val state: StateFlow<TransactionsState> = combine(
        userRepository.user.map { it?.isAdmin == true },
        filter,
        list,
        isRefreshing,
        isExporting,
        chart,
    ) { admin, filterState, listContent, refreshing, exporting, chartContent ->
        TransactionsState(
            isAdmin = admin,
            filter = filterState,
            list = listContent,
            isRefreshing = refreshing,
            isExporting = exporting,
            chart = chartContent,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TransactionsState(isAdmin = isAdmin),
    )

    init {
        reload()
    }

    fun onAction(action: TransactionsAction) {
        Logger.d("Action:Transactions", action.toString())
        when (action) {
            TransactionsAction.Refresh -> refresh()
            TransactionsAction.Retry -> reload()
            TransactionsAction.LoadMore -> loadMore()
            TransactionsAction.Export -> export()
            TransactionsAction.RetryChart -> loadChart()
            is TransactionsAction.SetOnlyMine -> updateFilter(filter.value.copy(onlyMine = action.onlyMine))
            is TransactionsAction.SelectType -> updateFilter(filter.value.copy(type = action.type))
        }
    }

    private fun updateFilter(newFilter: FilterState) {
        if (newFilter == filter.value) return
        filter.value = newFilter
        reload()
    }

    private fun reload() {
        list.value = ListContent.Loading
        loadFirstPage()
        loadChart()
    }

    private fun refresh() {
        if (isRefreshing.value) return
        isRefreshing.value = true
        loadFirstPage { isRefreshing.value = false }
        loadChart()
    }

    private fun loadFirstPage(onFinished: () -> Unit = {}) {
        listJob?.cancel()
        listJob = viewModelScope.launch {
            transactionRepository.getTransactions(currentFilter(), page = 0, size = PAGE_SIZE)
                .onSuccess { page ->
                    nextPage = 1
                    list.value = ListContent.Loaded(
                        items = page.content.map { it.toDisplay(dateFormatter, numberFormatter) }.toImmutableList(),
                        canLoadMore = page.totalPages > 1,
                        loadMore = LoadMoreStatus.Idle,
                    )
                }
                .onFailure { error ->
                    if (list.value is ListContent.Loaded) errorReporter.report(error) else list.value = ListContent.Failed(error.toFailureReason())
                }
            onFinished()
        }
    }

    private fun loadMore() {
        val current = list.value as? ListContent.Loaded ?: return
        if (!current.canLoadMore || current.loadMore == LoadMoreStatus.Loading) return
        list.value = current.copy(loadMore = LoadMoreStatus.Loading)
        listJob = viewModelScope.launch {
            transactionRepository.getTransactions(currentFilter(), page = nextPage, size = PAGE_SIZE)
                .onSuccess { page ->
                    nextPage++
                    list.value = current.copy(
                        items = (current.items + page.content.map { it.toDisplay(dateFormatter, numberFormatter) }).toImmutableList(),
                        canLoadMore = nextPage < page.totalPages,
                        loadMore = LoadMoreStatus.Idle,
                    )
                }
                .onFailure { list.value = current.copy(loadMore = LoadMoreStatus.Failed) }
        }
    }

    private fun loadChart() {
        chartJob?.cancel()
        if (!isAdmin) {
            chart.value = ChartContent.Hidden
            return
        }
        chart.value = ChartContent.Loading
        chartJob = viewModelScope.launch {
            chart.value = transactionRepository.getMonthlyKwh(currentFilter()).fold(
                onSuccess = { it.toChartContent() },
                onFailure = { ChartContent.Failed },
            )
        }
    }

    private fun export() {
        if (isExporting.value) return
        viewModelScope.launch {
            isExporting.value = true
            exportManager.exportAndShare(currentFilter()).onFailure { errorReporter.report(it) }
            isExporting.value = false
        }
    }

    private fun currentFilter(): TransactionFilter {
        val filterState = filter.value
        val admin = isAdmin
        return TransactionFilter(
            onlyAccountKeycloakId = userRepository.user.value?.id?.takeIf { admin && filterState.onlyMine },
            type = if (admin) filterState.type else TransactionType.All,
        )
    }

    private fun MonthlyKwhChart.toChartContent(): ChartContent {
        val maxTotal = months.indices.maxOfOrNull { index -> series.sumOf { it.kwh.getOrElse(index) { 0.0 } } } ?: 0.0
        return ChartContent.Loaded(
            labels = months.map { it.toMonthLabel() }.toImmutableList(),
            series = series.map { ChartSeries(name = it.name, isOther = it.isOther, values = it.kwh.toImmutableList()) }.toImmutableList(),
            maxValueText = numberFormatter.formatKwh(maxTotal),
        )
    }
}

internal fun String.toMonthLabel(): String {
    val parts = split("-")
    if (parts.size < 2) return this
    val month = parts[1].toIntOrNull() ?: return this
    return "$month/${parts[0].takeLast(2)}"
}
