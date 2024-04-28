package sk.momosilabs.trucker.server.security.controller

import sk.momosilabs.trucker.api.security.dto.CurrentUserDTO
import sk.momosilabs.trucker.server.security.model.TruckerPrincipal

fun TruckerPrincipal.toDto() = CurrentUserDTO(
    id = 0L,
    provider = "?",
    idKeycloak = idKeycloak,
    firstName = firstName,
    lastName = lastName,
    email = email,
)
