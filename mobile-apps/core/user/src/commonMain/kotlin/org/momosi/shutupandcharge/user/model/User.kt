package org.momosi.shutupandcharge.user.model

data class User(val id: String, val name: String, val email: String, val username: String, val roles: Set<UserRole>) {
    val isAdmin: Boolean get() = UserRole.Admin in roles
}
