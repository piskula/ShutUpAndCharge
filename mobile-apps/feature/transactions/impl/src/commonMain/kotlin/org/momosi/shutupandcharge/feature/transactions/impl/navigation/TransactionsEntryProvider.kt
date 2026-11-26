package org.momosi.shutupandcharge.feature.transactions.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.feature.transactions.api.TransactionsNavKey
import org.momosi.shutupandcharge.feature.transactions.impl.TransactionsScreen

fun EntryProviderScope<NavKey>.transactionsEntries() {
    entry<TransactionsNavKey> {
        TransactionsScreen()
    }
}
