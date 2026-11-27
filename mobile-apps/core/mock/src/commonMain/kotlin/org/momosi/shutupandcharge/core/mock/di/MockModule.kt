package org.momosi.shutupandcharge.core.mock.di

import org.koin.dsl.module
import org.momosi.shutupandcharge.core.account.AccountRepository
import org.momosi.shutupandcharge.core.charger.ChargerRepository
import org.momosi.shutupandcharge.core.common.version.VersionRepository
import org.momosi.shutupandcharge.core.mock.MockAccountRepository
import org.momosi.shutupandcharge.core.mock.MockAuthManager
import org.momosi.shutupandcharge.core.mock.MockBackend
import org.momosi.shutupandcharge.core.mock.MockChargerRepository
import org.momosi.shutupandcharge.core.mock.MockTransactionRepository
import org.momosi.shutupandcharge.core.mock.MockVersionRepository
import org.momosi.shutupandcharge.core.transaction.TransactionRepository
import org.momosi.shutupandcharge.user.AuthManager
import org.momosi.shutupandcharge.user.UserRepository

/** Overrides every backend/Keycloak-facing binding with in-memory fakes. Load it after the real modules. */
val mockModule = module {
    single { MockBackend() }
    single { MockAuthManager() }
    single<AuthManager> { get<MockAuthManager>() }
    single<UserRepository> { get<MockAuthManager>() }
    single<ChargerRepository> { MockChargerRepository(get(), get()) }
    single<TransactionRepository> { MockTransactionRepository(get()) }
    single<AccountRepository> { MockAccountRepository(get()) }
    single<VersionRepository> { MockVersionRepository(get()) }
}
