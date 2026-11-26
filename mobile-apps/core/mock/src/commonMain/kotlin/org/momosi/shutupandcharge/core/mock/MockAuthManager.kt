package org.momosi.shutupandcharge.core.mock

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.momosi.shutupandcharge.user.AuthManager
import org.momosi.shutupandcharge.user.UserRepository
import org.momosi.shutupandcharge.user.model.AuthActionResult
import org.momosi.shutupandcharge.user.model.AuthenticationState
import org.momosi.shutupandcharge.user.model.TokenResponse
import org.momosi.shutupandcharge.user.model.User

private const val MOCK_TOKEN = "mock-access-token"

/** Signs in [DemoUser] (User + Admin) without Keycloak; the session lives only in memory. */
internal class MockAuthManager :
    AuthManager,
    UserRepository {

    private val state = MutableStateFlow(AuthenticationState.Guest)
    private val currentUser = MutableStateFlow<User?>(null)
    private val inProgress = MutableStateFlow(false)
    private val completedLogins = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override val authenticationState: StateFlow<AuthenticationState> = state.asStateFlow()
    override val authenticationInProgress: StateFlow<Boolean> = inProgress.asStateFlow()
    override val loginCompletions: Flow<Unit> = completedLogins
    override val user: StateFlow<User?> = currentUser.asStateFlow()

    override suspend fun signIn(): AuthActionResult {
        inProgress.value = true
        delay(MOCK_LATENCY)
        currentUser.value = DemoUser
        state.value = AuthenticationState.Authorized
        inProgress.value = false
        completedLogins.tryEmit(Unit)
        return AuthActionResult.Success(AuthenticationState.Authorized)
    }

    override suspend fun signOut(): AuthActionResult {
        delay(MOCK_LATENCY)
        clearSession()
        return AuthActionResult.Success(AuthenticationState.Guest)
    }

    override suspend fun token(): TokenResponse = if (state.value == AuthenticationState.Authorized) TokenResponse.Token(MOCK_TOKEN) else TokenResponse.GuestWithoutToken

    override suspend fun invalidateSession(rejectedAccessToken: String) = clearSession()

    private fun clearSession() {
        currentUser.value = null
        state.value = AuthenticationState.Guest
    }
}
