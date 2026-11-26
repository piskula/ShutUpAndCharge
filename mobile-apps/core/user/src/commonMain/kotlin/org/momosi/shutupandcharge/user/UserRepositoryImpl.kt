package org.momosi.shutupandcharge.user

import kotlinx.coroutines.flow.StateFlow
import org.momosi.shutupandcharge.user.internal.UserStorage
import org.momosi.shutupandcharge.user.model.User

class UserRepositoryImpl(private val userStorage: UserStorage) : UserRepository {

    override val user: StateFlow<User?> = userStorage.userFlow
}
