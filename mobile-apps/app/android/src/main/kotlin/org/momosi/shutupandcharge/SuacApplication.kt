package org.momosi.shutupandcharge

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.momosi.shutupandcharge.app.initApp
import org.momosi.shutupandcharge.app.initKoin
import org.momosi.shutupandcharge.core.common.config.AppConfig

class SuacApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(appConfig()) { androidContext(this@SuacApplication) }
        initApp(isDebug = BuildConfig.DEBUG)
    }

    private fun appConfig() = AppConfig(
        apiBaseUrl = BuildConfig.API_BASE_URL,
        realmUrl = BuildConfig.REALM_URL,
        oauthClientId = BuildConfig.OAUTH_CLIENT_ID,
        appScheme = BuildConfig.APP_SCHEME,
        useMockData = BuildConfig.USE_MOCK_DATA,
    )
}
