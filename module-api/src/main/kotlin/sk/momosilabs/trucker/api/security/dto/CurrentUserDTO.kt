package sk.momosilabs.trucker.api.security.dto

data class CurrentUserDTO(
    val id: Long,
    val provider: String,
    val idKeycloak: String,
    val firstName: String,
    val lastName: String,
    val role: CurrentUserRoleDTO?,
)
