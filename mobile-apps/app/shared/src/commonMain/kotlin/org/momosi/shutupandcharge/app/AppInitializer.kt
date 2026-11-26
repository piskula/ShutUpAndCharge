package org.momosi.shutupandcharge.app

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.momosi.shutupandcharge.app.di.allModules
import org.momosi.shutupandcharge.core.common.config.AppConfig
import org.momosi.shutupandcharge.core.common.crashreporting.CrashReporting
import org.momosi.shutupandcharge.core.common.environment.AppEnvironment
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.mock.di.mockModule

fun initKoin(appConfig: AppConfig, platformConfig: KoinApplication.() -> Unit = {}) {
    startKoin {
        platformConfig()
        modules(module { single { appConfig } })
        modules(allModules)
        if (appConfig.useMockData) {
            allowOverride(true)
            modules(mockModule)
        }
    }
}

fun initApp(isDebug: Boolean) {
    AppEnvironment.init(isDebug)
    Logger.init(logToConsole = isDebug)
    CrashReporting.init()
}
