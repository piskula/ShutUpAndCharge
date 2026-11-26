package org.momosi.shutupandcharge.feature.profile.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.feature.profile.api.ProfileNavKey
import org.momosi.shutupandcharge.feature.profile.impl.ProfileScreen

fun EntryProviderScope<NavKey>.profileEntries() {
    entry<ProfileNavKey> {
        ProfileScreen()
    }
}
