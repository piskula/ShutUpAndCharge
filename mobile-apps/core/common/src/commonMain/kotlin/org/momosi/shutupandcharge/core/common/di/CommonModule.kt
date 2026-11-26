package org.momosi.shutupandcharge.core.common.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.common.coroutines.DispatcherProvider
import org.momosi.shutupandcharge.core.common.error.ErrorReporter
import org.momosi.shutupandcharge.core.common.error.ErrorReporterImpl
import org.momosi.shutupandcharge.core.common.formatter.DateFormatter
import org.momosi.shutupandcharge.core.common.formatter.NumberFormatter
import org.momosi.shutupandcharge.core.common.version.VersionRepository
import org.momosi.shutupandcharge.core.common.version.VersionRepositoryImpl
import org.momosi.shutupandcharge.core.common.version.api.VersionApi

val commonModule = module {
    single { DispatcherProvider() }
    single { DateFormatter() }
    single { NumberFormatter() }
    single { VersionApi(get()) }
    single<VersionRepository> { VersionRepositoryImpl(get()) }
    single<ErrorReporter> { ErrorReporterImpl() }
}

expect fun platformCommonModule(): Module
