package org.momosi.shutupandcharge.app

import org.momosi.shutupandcharge.core.common.config.AppConfig

fun bootstrapIosApp(
    isDebug: Boolean,
    apiBaseUrl: String,
    realmUrl: String,
    oauthClientId: String,
    appScheme: String,
    useMockData: Boolean,
) {
    initKoin(
        AppConfig(
            apiBaseUrl = apiBaseUrl,
            realmUrl = realmUrl,
            oauthClientId = oauthClientId,
            appScheme = appScheme,
            useMockData = useMockData,
        ),
    )
    initApp(isDebug = isDebug)
}
