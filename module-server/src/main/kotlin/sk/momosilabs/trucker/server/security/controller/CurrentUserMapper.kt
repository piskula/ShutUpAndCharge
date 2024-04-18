package sk.momosilabs.trucker.server.security.controller

import sk.momosilabs.trucker.api.security.dto.CurrentUserDTO
import sk.momosilabs.trucker.server.security.model.CurrentUser

fun CurrentUser.toDto() = CurrentUserDTO(
    id = 0L,
    provider = provider,
    idFromProvider = identifier,
    firstName = firstName,
    lastName = lastName,
    email = email,
)
