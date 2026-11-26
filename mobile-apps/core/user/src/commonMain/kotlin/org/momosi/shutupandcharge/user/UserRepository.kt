package org.momosi.shutupandcharge.user

import kotlinx.coroutines.flow.StateFlow
import org.momosi.shutupandcharge.user.model.User

interface UserRepository {

    val user: StateFlow<User?>
}
