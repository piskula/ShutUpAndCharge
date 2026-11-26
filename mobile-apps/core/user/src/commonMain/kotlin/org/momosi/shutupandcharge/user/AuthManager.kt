package org.momosi.shutupandcharge.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.momosi.shutupandcharge.user.model.AuthActionResult
import org.momosi.shutupandcharge.user.model.AuthenticationState
import org.momosi.shutupandcharge.user.model.TokenResponse

interface AuthManager {

    val authenticationState: StateFlow<AuthenticationState>
    val authenticationInProgress: StateFlow<Boolean>
    val loginCompletions: Flow<Unit>

    suspend fun signIn(): AuthActionResult
    suspend fun signOut(): AuthActionResult

    suspend fun token(): TokenResponse
    suspend fun invalidateSession(rejectedAccessToken: String)
}
