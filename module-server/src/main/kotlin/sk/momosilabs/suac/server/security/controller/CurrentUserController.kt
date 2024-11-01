package sk.momosilabs.suac.server.security.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.security.CurrentUserApi
import sk.momosilabs.suac.api.security.dto.CurrentUserDTO
import sk.momosilabs.suac.server.security.service.CurrentUserService

@RestController
class CurrentUserController(
    private val currentUserService: CurrentUserService,
) : CurrentUserApi {

    override fun getCurrentUser(): ResponseEntity<CurrentUserDTO> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth is AnonymousAuthenticationToken)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return ResponseEntity.ok().body(
            currentUserService.getCurrentUser().toDto()
        )
    }

}
