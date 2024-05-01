package sk.momosilabs.trucker.server.security.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import sk.momosilabs.trucker.server.account.entity.AccountRole

data class TruckerPrincipal(
    val defaultUser: DefaultOidcUser,

    val idKeycloak: String,
    val firstName: String,
    val lastName: String,
    val role: AccountRole?,
) : OidcUser {
    override fun getName(): String = defaultUser.name
    override fun getAttributes(): MutableMap<String, Any> = defaultUser.attributes
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = defaultUser.authorities
    override fun getClaims(): MutableMap<String, Any> = defaultUser.claims
    override fun getUserInfo(): OidcUserInfo = defaultUser.userInfo
    override fun getIdToken(): OidcIdToken = defaultUser.idToken
}
