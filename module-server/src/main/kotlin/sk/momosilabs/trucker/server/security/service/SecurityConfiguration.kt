package sk.momosilabs.trucker.server.security.service

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
open class SecurityConfiguration(
    private val keycloakLogoutHandler: KeycloakLogoutHandler,
) {

    companion object {
        private const val GROUPS = "groups"
        private const val REALM_ACCESS_CLAIM = "realm_access"
        private const val ROLES_CLAIM = "roles"

        private val SWAGGER_RESOURCES = listOf(
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
        ).toTypedArray()

        private val STATIC_RESOURCES = listOf(
            "**.html",
            "**.js",
            "**.css",
        ).toTypedArray()

        private val logger = LoggerFactory.getLogger(KeycloakLogoutHandler::class.java)
    }

    @Bean
    open fun sessionRegistry(): SessionRegistry = SessionRegistryImpl()

    @Bean
    protected open fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy =
        RegisterSessionAuthenticationStrategy(sessionRegistry())

    @Bean
    open fun httpSessionEventPublisher(): HttpSessionEventPublisher = HttpSessionEventPublisher()

    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { auth ->
            auth
                .requestMatchers(AntPathRequestMatcher("/api/version")).permitAll()
                .requestMatchers(*SWAGGER_RESOURCES).permitAll()
                .requestMatchers(AntPathRequestMatcher("/api*", HttpMethod.OPTIONS.name())).permitAll()
                .requestMatchers(AntPathRequestMatcher("/api*")).hasRole("user")
                .requestMatchers(*STATIC_RESOURCES).permitAll()
                .requestMatchers(AntPathRequestMatcher("/")).permitAll()
                .anyRequest().authenticated()
        }
        http.oauth2ResourceServer { oauth2 ->
            oauth2.jwt(Customizer.withDefaults())
        }
        http.oauth2Login(Customizer.withDefaults())
            .logout { logout -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/") }
        return http.build()
    }

    @Bean
    open fun userAuthoritiesMapperForKeycloak(): GrantedAuthoritiesMapper {
        return GrantedAuthoritiesMapper { authorities: Collection<GrantedAuthority> ->
            val mappedAuthorities: MutableSet<GrantedAuthority?> = HashSet()
            val authority = authorities.iterator().next()
            val isOidc = authority is OidcUserAuthority

            if (isOidc) {
                val oidcUserAuthority = authority as OidcUserAuthority
                val userInfo = oidcUserAuthority.userInfo

                // Tokens can be configured to return roles under
                // Groups or REALM ACCESS hence have to check both
                if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) {
                    val realmAccess = userInfo.getClaimAsMap(REALM_ACCESS_CLAIM)
                    val roles = realmAccess[ROLES_CLAIM] as Collection<String?>?
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles!!))
                } else if (userInfo.hasClaim(GROUPS)) {
                    val roles = userInfo.getClaim<Any>(GROUPS) as Collection<String?>
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles))
                }
            } else {
                val oauth2UserAuthority = authority as OAuth2UserAuthority
                val userAttributes = oauth2UserAuthority.attributes

                if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) {
                    val realmAccess = userAttributes[REALM_ACCESS_CLAIM] as Map<String, Any>?
                    val roles = realmAccess!![ROLES_CLAIM] as Collection<String?>?
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles!!))
                }
            }
            if (mappedAuthorities.isEmpty())
                logger.warn("User has no authorities! (cannot fetch them)")
            mappedAuthorities
        }
    }

    // Spring Security generally adds “ROLE_” prefix to each role name, whereas Keycloak sends plain role names across
    private fun generateAuthoritiesFromClaim(roles: Collection<*>): Collection<GrantedAuthority> {
        return roles.map { role -> SimpleGrantedAuthority("ROLE_$role") }
    }

}
