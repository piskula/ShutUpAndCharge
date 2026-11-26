package org.momosi.shutupandcharge.feature.transactions.impl.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.momosi.shutupandcharge.feature.transactions.impl.TransactionsViewModel

val transactionsModule = module {
    viewModel { TransactionsViewModel(get(), get(), get(), get(), get(), get()) }
}
