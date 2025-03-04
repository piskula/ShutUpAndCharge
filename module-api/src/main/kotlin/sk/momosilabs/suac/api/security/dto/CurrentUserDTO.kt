package sk.momosilabs.suac.api.security.dto

data class CurrentUserDTO(
    val id: Long,
    val provider: String,
    val idKeycloak: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<CurrentUserRoleDTO>,
)
