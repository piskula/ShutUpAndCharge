package org.momosi.shutupandcharge.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.clearAuthTokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.momosi.shutupandcharge.user.AuthManager

fun HttpClient.invalidateAuthTokensOn(authManager: AuthManager, scope: CoroutineScope) {
    authManager.authenticationState
        .onEach { clearAuthTokens() }
        .launchIn(scope)
}
