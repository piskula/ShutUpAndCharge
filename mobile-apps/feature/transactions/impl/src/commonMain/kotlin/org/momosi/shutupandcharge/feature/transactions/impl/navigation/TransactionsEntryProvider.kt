package org.momosi.shutupandcharge.feature.transactions.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.core.navigation.Navigator
import org.momosi.shutupandcharge.feature.transactions.api.TransactionsNavKey
import org.momosi.shutupandcharge.feature.transactions.impl.TransactionsScreen
import org.momosi.shutupandcharge.feature.transactions.impl.monthly.MonthlyUsageScreen

fun EntryProviderScope<NavKey>.transactionsEntries(navigator: Navigator) {
    entry<TransactionsNavKey> {
        TransactionsScreen(onOpenMonthlyUsage = { onlyMine -> navigator.navigate(MonthlyUsageNavKey(onlyMine)) })
    }
    entry<MonthlyUsageNavKey> { key ->
        MonthlyUsageScreen(onlyMine = key.onlyMine, onBack = navigator::goBack)
    }
}
