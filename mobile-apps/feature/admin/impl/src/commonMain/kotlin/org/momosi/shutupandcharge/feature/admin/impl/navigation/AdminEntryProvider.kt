package org.momosi.shutupandcharge.feature.admin.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.feature.admin.api.AdminNavKey
import org.momosi.shutupandcharge.feature.admin.impl.AdminScreen

fun EntryProviderScope<NavKey>.adminEntries() {
    entry<AdminNavKey> {
        AdminScreen()
    }
}
