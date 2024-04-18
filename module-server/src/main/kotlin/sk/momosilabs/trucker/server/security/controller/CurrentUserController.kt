package sk.momosilabs.trucker.server.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.trucker.api.security.CurrentUserApi
import sk.momosilabs.trucker.api.security.dto.CurrentUserDTO
import sk.momosilabs.trucker.server.security.service.CurrentUserService

@RestController
class CurrentUserController(
    private val currentUserService: CurrentUserService,
) : CurrentUserApi {

    override fun get(): ResponseEntity<CurrentUserDTO> =
        ResponseEntity.ok().body(
            currentUserService.getCurrentUser().toDto()
        )

}
