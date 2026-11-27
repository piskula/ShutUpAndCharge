package org.momosi.shutupandcharge.feature.transactions.impl.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder

@Serializable
internal data class MonthlyUsageNavKey(val onlyMine: Boolean) : NavKey

fun PolymorphicModuleBuilder<NavKey>.transactionsNavKeySubclasses() {
    subclass(MonthlyUsageNavKey::class, MonthlyUsageNavKey.serializer())
}
