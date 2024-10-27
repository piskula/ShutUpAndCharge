package sk.momosilabs.trucker.server.security.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.trucker.api.security.CurrentUserApi
import sk.momosilabs.trucker.api.security.dto.CurrentUserDTO
import sk.momosilabs.trucker.server.security.service.CurrentUserService

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

    override fun postSecured(thisIsBody: String): ResponseEntity<String> {
        return ResponseEntity.ok("This was posted: $thisIsBody")
    }

    override fun getSecured(): ResponseEntity<String> {
        return ResponseEntity.ok("Why am I here?")
    }

}
