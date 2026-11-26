package org.momosi.shutupandcharge.app

import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.feature.admin.api.AdminNavKey
import org.momosi.shutupandcharge.feature.dashboard.api.DashboardNavKey
import org.momosi.shutupandcharge.feature.profile.api.ProfileNavKey
import org.momosi.shutupandcharge.feature.transactions.api.TransactionsNavKey

internal enum class MainTab(val navKey: NavKey) {
    Dashboard(DashboardNavKey),
    Transactions(TransactionsNavKey),
    Admin(AdminNavKey),
    Profile(ProfileNavKey),
}

internal fun mainTabsFor(isAdmin: Boolean): List<MainTab> = MainTab.entries.filter { it != MainTab.Admin || isAdmin }

internal fun NavKey.isMainTab(): Boolean = MainTab.entries.any { it.navKey == this }
