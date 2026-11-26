package org.momosi.shutupandcharge.core.account.di

import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.account.AccountRepository
import org.momosi.shutupandcharge.core.account.AccountRepositoryImpl
import org.momosi.shutupandcharge.core.account.api.createAccountApi

val accountModule = module {
    single { get<Ktorfit>().createAccountApi() }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
}
