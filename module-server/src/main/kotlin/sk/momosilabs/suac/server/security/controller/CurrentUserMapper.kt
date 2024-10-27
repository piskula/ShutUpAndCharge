package sk.momosilabs.suac.server.security.controller

import sk.momosilabs.suac.api.security.dto.CurrentUserDTO
import sk.momosilabs.suac.api.security.dto.CurrentUserRoleDTO
import sk.momosilabs.suac.server.security.model.TruckerPrincipal

fun TruckerPrincipal.toDto() = CurrentUserDTO(
    id = 0L,
    provider = "?",
    idKeycloak = idKeycloak,
    firstName = firstName,
    lastName = lastName,
    role = role?.let { CurrentUserRoleDTO.valueOf(it.name) },
)
