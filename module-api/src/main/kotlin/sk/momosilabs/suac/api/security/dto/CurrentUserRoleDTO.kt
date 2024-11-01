package sk.momosilabs.suac.api.security.dto

enum class CurrentUserRoleDTO {
    User,
    Admin;

    companion object {
        fun fromKeycloakRole(keycloakRole: String): CurrentUserRoleDTO {
            return when (keycloakRole) {
                "MOMO_USER" -> User
                "MOMO_ADMIN" -> Admin
                else -> throw IllegalArgumentException("keycloakRole $keycloakRole not supported")
            }
        }
    }
}
