package org.momosi.shutupandcharge.feature.transactions.impl

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import org.momosi.shutupandcharge.core.common.network.FailureReason
import org.momosi.shutupandcharge.core.transaction.display.TransactionDisplay
import org.momosi.shutupandcharge.core.transaction.model.TransactionType

@Immutable
data class TransactionsState(val isAdmin: Boolean = false, val filter: FilterState = FilterState(), val list: ListContent = ListContent.Loading, val isRefreshing: Boolean = false, val isExporting: Boolean = false)

@Immutable
data class FilterState(val onlyMine: Boolean = false, val type: TransactionType = TransactionType.All)

@Immutable
sealed interface ListContent {
    data object Loading : ListContent
    data class Failed(val reason: FailureReason) : ListContent
    data class Loaded(val items: ImmutableList<TransactionDisplay>, val canLoadMore: Boolean, val loadMore: LoadMoreStatus) : ListContent
}

enum class LoadMoreStatus { Idle, Loading, Failed }
