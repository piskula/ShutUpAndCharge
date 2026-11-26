package org.momosi.shutupandcharge.feature.dashboard.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.core.navigation.Navigator
import org.momosi.shutupandcharge.feature.dashboard.api.DashboardNavKey
import org.momosi.shutupandcharge.feature.dashboard.impl.DashboardScreen
import org.momosi.shutupandcharge.feature.transactions.api.TransactionsNavKey

fun EntryProviderScope<NavKey>.dashboardEntries(navigator: Navigator) {
    entry<DashboardNavKey> {
        DashboardScreen(
            onShowAllTransactions = { navigator.navigate(root = DashboardNavKey, destination = TransactionsNavKey) },
        )
    }
}
