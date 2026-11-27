package org.momosi.shutupandcharge.feature.transactions.impl

import org.momosi.shutupandcharge.core.transaction.model.TransactionType

sealed interface TransactionsAction {
    data object Refresh : TransactionsAction
    data object Retry : TransactionsAction
    data object LoadMore : TransactionsAction
    data object Export : TransactionsAction
    data class SetOnlyMine(val onlyMine: Boolean) : TransactionsAction
    data class SelectType(val type: TransactionType) : TransactionsAction
}
