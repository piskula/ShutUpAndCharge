package sk.momosilabs.trucker.server.security.service

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Service

@Service
open class OnAuthenticationConfig(
//    private val accountPersistence: AccountPersistence,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(OnAuthenticationConfig::class.java)
    }

    @EventListener
    fun onAuthenticationSuccess(successEvent: AuthenticationSuccessEvent) {
        val source = successEvent.source

//        if (source is JwtAuthenticationToken) {
//            val user = source.tokenToUser()
//
//            logger.info("user logged in through ${user.provider} with identifier ${user.identifier}")
////            when (user.provider) {
////                TODO
////            }
////            if (user is CurrentGoogleUser) {
////                if (!accountPersistence.existsBy(provider = user.provider, identifierWithinProvider = user.identifier)) {
////                    accountPersistence.create(user)
////                }
////            }
//        }
    }

}
