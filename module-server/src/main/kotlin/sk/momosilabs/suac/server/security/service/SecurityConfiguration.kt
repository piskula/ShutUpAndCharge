package sk.momosilabs.suac.server.security.service

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.header.writers.HstsHeaderWriter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.header.writers.XContentTypeOptionsHeaderWriter
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
open class SecurityConfiguration(
    private val keycloakLogoutHandler: KeycloakLogoutHandler,
) {

    @Bean
    open fun sessionRegistry(): SessionRegistry = SessionRegistryImpl()

    @Bean
    protected open fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy =
        RegisterSessionAuthenticationStrategy(sessionRegistry())

    @Bean
    open fun httpSessionEventPublisher(): HttpSessionEventPublisher = HttpSessionEventPublisher()

    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
//            .csrf { it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) }
            .authorizeHttpRequests { auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/api/currentUser/**").permitAll()
                .requestMatchers(AntPathRequestMatcher("/api/**")).hasRole("USER")
                .anyRequest().permitAll()
            }
            .oauth2Login(Customizer.withDefaults())
                .logout { logout -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/") }
            .headers {
                it.addHeaderWriter(XContentTypeOptionsHeaderWriter())
                it.addHeaderWriter(XFrameOptionsHeaderWriter())
                it.addHeaderWriter(ReferrerPolicyHeaderWriter()) // or comment out?
                it.addHeaderWriter(XXssProtectionHeaderWriter())
                it.addHeaderWriter(HstsHeaderWriter())
            }
            .anonymous { it.disable() } // do not allow anonymous user in SecurityContext

        return http.build()
    }

    @Bean
    open fun configureWebSecurity(): WebSecurityCustomizer =
        WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        }

}
