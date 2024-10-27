package sk.momosilabs.suac.server.security.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import sk.momosilabs.suac.server.security.model.TruckerPrincipal

@Service
open class CurrentUserService {

    fun getCurrentUser(): TruckerPrincipal =
        (SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken)
            .principal as TruckerPrincipal

    fun userId(): String = getCurrentUser().idKeycloak

}
