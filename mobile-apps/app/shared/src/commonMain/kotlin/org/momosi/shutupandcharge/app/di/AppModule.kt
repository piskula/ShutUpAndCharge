package org.momosi.shutupandcharge.app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.momosi.shutupandcharge.app.CrashReportingSync
import org.momosi.shutupandcharge.app.SuacViewModel
import org.momosi.shutupandcharge.core.account.di.accountModule
import org.momosi.shutupandcharge.core.charger.di.chargerModule
import org.momosi.shutupandcharge.core.common.di.commonModule
import org.momosi.shutupandcharge.core.common.di.platformCommonModule
import org.momosi.shutupandcharge.core.network.di.networkModule
import org.momosi.shutupandcharge.core.transaction.di.platformTransactionModule
import org.momosi.shutupandcharge.core.transaction.di.transactionModule
import org.momosi.shutupandcharge.feature.admin.impl.di.adminModule
import org.momosi.shutupandcharge.feature.dashboard.impl.di.dashboardModule
import org.momosi.shutupandcharge.feature.profile.impl.di.profileModule
import org.momosi.shutupandcharge.feature.transactions.impl.di.transactionsModule
import org.momosi.shutupandcharge.feature.welcome.impl.di.welcomeModule
import org.momosi.shutupandcharge.user.di.platformUserModule
import org.momosi.shutupandcharge.user.di.userModule

val appModule = module {
    single { CoroutineScope(SupervisorJob()) }
    single(createdAtStart = true) { CrashReportingSync(get(), get(), get()) }
    viewModel { SuacViewModel(get(), get(), get()) }
}

val allModules: List<Module> = listOf(
    commonModule,
    platformCommonModule(),
    networkModule,
    userModule,
    platformUserModule(),
    chargerModule,
    transactionModule,
    platformTransactionModule(),
    accountModule,
    appModule,
    welcomeModule,
    dashboardModule,
    transactionsModule,
    adminModule,
    profileModule,
)
