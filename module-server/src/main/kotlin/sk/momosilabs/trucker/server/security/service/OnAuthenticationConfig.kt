package sk.momosilabs.trucker.server.security.service

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import sk.momosilabs.trucker.server.account.entity.AccountEntity
import sk.momosilabs.trucker.server.account.repository.AccountRepository
import sk.momosilabs.trucker.server.security.model.TruckerPrincipal
import sk.momosilabs.trucker.server.security.model.UserTokenClaims
import java.net.URL
import kotlin.jvm.optionals.getOrNull

@Service
open class OnAuthenticationConfig(
    private val accountRepository: AccountRepository,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(OnAuthenticationConfig::class.java)

        fun DefaultOidcUser.tokenClaims(): UserTokenClaims {
            val email = idToken.claims["email"] as String
            val sub = idToken.claims["sub"] as String
            val iss = idToken.claims["iss"] as URL
            val firstName = idToken.claims["given_name"] as String
            val lastName = idToken.claims["family_name"] as String

            return UserTokenClaims(
                email = email,
                identifierSub = sub,
                provider = iss.toString(),
                firstName = firstName,
                lastName = lastName,
            )
        }
    }

    @EventListener
    fun onAuthenticationSuccess(successEvent: AuthenticationSuccessEvent) {
        val source = successEvent.source

        if (source is OAuth2LoginAuthenticationToken) {
            val principal = source.principal
            if (principal is TruckerPrincipal) {
                val existingUser = accountRepository.findByIdKeycloak(principal.idKeycloak)
                if (existingUser == null) {
                    accountRepository.save(principal.toAccountEntity())
                }
            }
        }
    }

    @Bean
    open fun oidcUserService(): OAuth2UserService<OidcUserRequest, OidcUser> {
        return (object: OidcUserService() {
            override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
                val userFromKeycloak = super.loadUser(userRequest) as DefaultOidcUser
                val claims = userFromKeycloak.tokenClaims()
                val userStored = accountRepository.findByIdKeycloak(claims.identifierSub)
                return TruckerPrincipal(
                    defaultUser = userFromKeycloak,
                    idKeycloak = claims.identifierSub,
                    firstName = userStored?.firstName ?: claims.firstName,
                    lastName = userStored?.lastName ?: claims.lastName,
                    role = userStored?.role,
                )
            }
        })
    }

    private fun TruckerPrincipal.toAccountEntity() = AccountEntity(
        idKeycloak = idKeycloak,
        firstName = firstName,
        lastName = lastName,
        role = null,
    )

}
