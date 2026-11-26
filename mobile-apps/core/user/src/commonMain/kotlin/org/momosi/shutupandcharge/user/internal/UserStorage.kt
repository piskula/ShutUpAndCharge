package org.momosi.shutupandcharge.user.internal

import kotlinx.coroutines.flow.StateFlow
import org.momosi.shutupandcharge.user.model.StoredTokens
import org.momosi.shutupandcharge.user.model.User

const val USER_AUTH_STORAGE = "user_auth_storage"

interface UserStorage {
    var tokens: StoredTokens?
    var serverPublicKey: String?
    var user: User?
    val userFlow: StateFlow<User?>
}
