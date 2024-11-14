package sk.momosilabs.suac.server.security.controller

import sk.momosilabs.suac.api.security.dto.CurrentUserDTO
import sk.momosilabs.suac.api.security.dto.CurrentUserRoleDTO.Companion.fromKeycloakRole
import sk.momosilabs.suac.server.security.model.TruckerPrincipal

fun TruckerPrincipal.toDto() = CurrentUserDTO(
    id = id,
    provider = "?",
    idKeycloak = idKeycloak,
    firstName = firstName,
    lastName = lastName,
    roles = momoRoles().mapTo(HashSet()) { fromKeycloakRole(it) },
)
