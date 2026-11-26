package org.momosi.shutupandcharge.app

import org.momosi.shutupandcharge.core.common.config.AppConfig

fun bootstrapIosApp(
    isDebug: Boolean,
    apiBaseUrl: String,
    realmUrl: String,
    oauthClientId: String,
    appScheme: String,
) {
    initKoin(
        AppConfig(
            apiBaseUrl = apiBaseUrl,
            realmUrl = realmUrl,
            oauthClientId = oauthClientId,
            appScheme = appScheme,
        ),
    )
    initApp(isDebug = isDebug)
}
