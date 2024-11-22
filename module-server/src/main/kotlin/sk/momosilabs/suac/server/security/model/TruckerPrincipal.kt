package sk.momosilabs.suac.server.security.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import sk.momosilabs.suac.api.security.dto.CurrentUserRoleDTO
import sk.momosilabs.suac.api.security.dto.CurrentUserRoleDTO.Companion.fromKeycloakRole

data class TruckerPrincipal(
    var id: Long,
    val defaultUser: DefaultOidcUser,

    val idKeycloak: String,
    val firstName: String,
    val lastName: String,
    val oidcAuthorities: Set<String>,
) : OidcUser {
    override fun getName(): String = defaultUser.name
    override fun getAttributes(): MutableMap<String, Any> = defaultUser.attributes
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = defaultUser.authorities
        .plus(oidcAuthorities.map { SimpleGrantedAuthority("ROLE_$it") }).toMutableList()
    override fun getClaims(): MutableMap<String, Any> = defaultUser.claims
    override fun getUserInfo(): OidcUserInfo = defaultUser.userInfo
    override fun getIdToken(): OidcIdToken = defaultUser.idToken

    fun momoRoles(): Set<CurrentUserRoleDTO> = authorities
        .filter { it.authority.startsWith("ROLE_MOMO_") }
        .mapTo(HashSet()) { fromKeycloakRole(it.authority.removePrefix("ROLE_")) }

}
