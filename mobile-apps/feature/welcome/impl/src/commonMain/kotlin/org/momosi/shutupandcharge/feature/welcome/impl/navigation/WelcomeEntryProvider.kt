package org.momosi.shutupandcharge.feature.welcome.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.momosi.shutupandcharge.feature.welcome.api.WelcomeNavKey
import org.momosi.shutupandcharge.feature.welcome.impl.WelcomeScreen

fun EntryProviderScope<NavKey>.welcomeEntries() {
    entry<WelcomeNavKey> {
        WelcomeScreen()
    }
}
