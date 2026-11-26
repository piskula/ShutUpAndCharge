package org.momosi.shutupandcharge.app

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.momosi.shutupandcharge.feature.admin.api.AdminNavKey
import org.momosi.shutupandcharge.feature.dashboard.api.DashboardNavKey
import org.momosi.shutupandcharge.feature.profile.api.ProfileNavKey
import org.momosi.shutupandcharge.feature.transactions.api.TransactionsNavKey
import org.momosi.shutupandcharge.feature.welcome.api.WelcomeNavKey

val appSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(WelcomeNavKey::class, WelcomeNavKey.serializer())
            subclass(DashboardNavKey::class, DashboardNavKey.serializer())
            subclass(TransactionsNavKey::class, TransactionsNavKey.serializer())
            subclass(AdminNavKey::class, AdminNavKey.serializer())
            subclass(ProfileNavKey::class, ProfileNavKey.serializer())
        }
    }
}
