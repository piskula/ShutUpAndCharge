package org.momosi.shutupandcharge.core.transaction.di

import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.module.Module
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.transaction.TransactionRepository
import org.momosi.shutupandcharge.core.transaction.TransactionRepositoryImpl
import org.momosi.shutupandcharge.core.transaction.api.createTransactionApi
import org.momosi.shutupandcharge.core.transaction.export.TransactionExportManager
import org.momosi.shutupandcharge.core.transaction.export.TransactionExportManagerImpl

val transactionModule = module {
    single { get<Ktorfit>().createTransactionApi() }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<TransactionExportManager> { TransactionExportManagerImpl(get(), get()) }
}

expect fun platformTransactionModule(): Module
