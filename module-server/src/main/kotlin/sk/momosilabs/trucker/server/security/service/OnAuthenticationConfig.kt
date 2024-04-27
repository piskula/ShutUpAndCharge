package sk.momosilabs.trucker.server.security.service

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.stereotype.Service
import sk.momosilabs.trucker.server.account.entity.AccountEntity
import sk.momosilabs.trucker.server.account.repository.AccountRepository
import sk.momosilabs.trucker.server.security.service.CurrentUserService.Companion.tokenToUser

@Service
open class OnAuthenticationConfig(
    private val accountRepository: AccountRepository,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(OnAuthenticationConfig::class.java)
    }

    @EventListener
    fun onAuthenticationSuccess(successEvent: AuthenticationSuccessEvent) {
        val source = successEvent.source

        if (source is OAuth2LoginAuthenticationToken) {
            val principal = source.principal
            if (principal is DefaultOidcUser) {
                val user = principal.tokenToUser()
                val existingUser = accountRepository.findById(user.identifier)
                if (existingUser.isEmpty) {
                    accountRepository.save(AccountEntity(user.identifier, user.firstName, user.lastName))
                }
            }
        }
    }

}
