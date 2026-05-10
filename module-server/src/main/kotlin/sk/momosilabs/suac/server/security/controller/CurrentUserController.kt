package sk.momosilabs.suac.server.security.controller

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.security.CurrentUserApi
import sk.momosilabs.suac.api.security.dto.CurrentUserDTO
import sk.momosilabs.suac.api.security.dto.CurrentUserRoleDTO
import sk.momosilabs.suac.api.security.dto.CurrentUserRoleDTO.Companion.fromKeycloakRole
import sk.momosilabs.suac.server.account.entity.AccountEntity
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository

@RestController
class CurrentUserController(
    private val accountRepository: AccountRepository,
) : CurrentUserApi {

    override fun getCurrentUser(jwt: Jwt): CurrentUserDTO {

        val sub = jwt.subject
        val firstName = jwt.getClaim<String>("given_name")
        val lastName = jwt.getClaim<String>("family_name")

        val roles = extractMomoRoles(jwt)

        val user = accountRepository.findByIdKeycloak(sub)
            ?: accountRepository.save(
                AccountEntity(
                    idKeycloak = sub,
                    firstName = firstName,
                    lastName = lastName,
                    verifiedForCharging = roles.contains(CurrentUserRoleDTO.Admin),
                    assignedChipUid = null,
                )
            ).also {
                // optional: log first login
            }

        // optional: update on each request (like your old logic)
        user.firstName = firstName
        user.lastName = lastName

        return CurrentUserDTO(
            id = user.id,
            provider = jwt.issuer.toString(),
            idKeycloak = sub,
            firstName = user.firstName,
            lastName = user.lastName,
            roles = roles
        )
    }

    private fun extractMomoRoles(jwt: Jwt): Set<CurrentUserRoleDTO> {
        val realmAccess = jwt.getClaim<Map<String, Any>>("realm_access")
        val roles = realmAccess?.get("roles") as? Collection<*>

        return roles
            ?.filterIsInstance<String>()
            ?.filter { it.startsWith("MOMO_") }
            ?.mapTo(HashSet()) { fromKeycloakRole(it) }
            ?: emptySet()
    }
}
