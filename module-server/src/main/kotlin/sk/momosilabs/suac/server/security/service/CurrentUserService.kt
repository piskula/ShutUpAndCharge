package sk.momosilabs.suac.server.security.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service

@Service
class CurrentUserService() {

    fun getJwt(): Jwt =
        (SecurityContextHolder.getContext().authentication as JwtAuthenticationToken).token

    fun keycloakId(): String = getJwt().subject

    fun email(): String = getJwt().getClaim("email")

    fun roles(): Set<String> {
        val realmAccess = getJwt().getClaim<Map<String, Any>>("realm_access")
        val roles = realmAccess?.get("roles") as? Collection<*>
        return roles?.filterIsInstance<String>()?.toSet() ?: emptySet()
    }

    fun isAdmin(): Boolean = roles().contains("MOMO_ADMIN")
    // TODO somehow use extractMomoRoles so we can work with ENUM

}
