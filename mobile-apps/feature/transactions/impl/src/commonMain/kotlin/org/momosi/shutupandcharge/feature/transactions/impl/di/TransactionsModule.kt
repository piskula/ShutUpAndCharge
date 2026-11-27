package org.momosi.shutupandcharge.feature.transactions.impl.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.momosi.shutupandcharge.feature.transactions.impl.TransactionsViewModel
import org.momosi.shutupandcharge.feature.transactions.impl.monthly.MonthlyUsageViewModel

val transactionsModule = module {
    viewModel { TransactionsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { (onlyMine: Boolean) -> MonthlyUsageViewModel(onlyMine, get(), get(), get(), get()) }
}
