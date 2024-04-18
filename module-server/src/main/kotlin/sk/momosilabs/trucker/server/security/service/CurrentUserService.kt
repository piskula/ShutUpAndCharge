package sk.momosilabs.trucker.server.security.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.stereotype.Service
import sk.momosilabs.trucker.server.security.model.CurrentUser
import java.net.URL

@Service
open class CurrentUserService {

    companion object {

        fun DefaultOidcUser.tokenToUser(): CurrentUser {
            val email = idToken.claims["email"] as String
            val sub = idToken.claims["sub"] as String
            val iss = idToken.claims["iss"] as URL
            val firstName = idToken.claims["given_name"] as String
            val lastName = idToken.claims["family_name"] as String

            return CurrentUser(
                email = email,
                identifier = sub,
                provider = iss.toString(),
                firstName = firstName,
                lastName = lastName,
            )
        }
    }

    fun getCurrentUser(): CurrentUser = (
        (SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken)
            .principal as DefaultOidcUser
        ).tokenToUser()

    fun identifier(): String = getCurrentUser().identifier

}
