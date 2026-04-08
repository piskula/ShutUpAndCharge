package sk.momosilabs.suac.server.security.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder


@Component
open class KeycloakLogoutHandler: LogoutHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(KeycloakLogoutHandler::class.java)
    }

    private val restClient = RestClient.create()

    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        logoutFromKeycloak(authentication?.principal as OidcUser)
    }

    private fun logoutFromKeycloak(user: OidcUser) {
        val endSessionEndpoint = user.issuer.toString() + "/protocol/openid-connect/logout"
        val builder = UriComponentsBuilder
            .fromUriString(endSessionEndpoint)
            .queryParam("id_token_hint", user.idToken.tokenValue)

        val logoutResponse = restClient.get()
            .uri(builder.toUriString())
            .retrieve()
            .toBodilessEntity()

        if (logoutResponse.statusCode.is2xxSuccessful) {
            logger.info("Successfully logged out from Keycloak")
        } else {
            logger.error("Could not propagate logout to Keycloak")
        }
    }
}
