package org.momosi.shutupandcharge.core.common.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.common.language.LanguageRepository
import org.momosi.shutupandcharge.core.common.language.LanguageRepositoryImpl
import org.momosi.shutupandcharge.core.common.network.ConnectivityManager
import org.momosi.shutupandcharge.core.common.network.ConnectivityManagerImpl
import org.momosi.shutupandcharge.core.common.version.AppVersionProvider
import org.momosi.shutupandcharge.core.common.version.AppVersionProviderImpl

actual fun platformCommonModule(): Module = module {
    single<ConnectivityManager> { ConnectivityManagerImpl() }
    single<AppVersionProvider> { AppVersionProviderImpl() }
    single<LanguageRepository> { LanguageRepositoryImpl() }
}
