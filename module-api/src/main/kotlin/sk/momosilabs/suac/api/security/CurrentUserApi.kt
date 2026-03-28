package sk.momosilabs.suac.api.security

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.security.dto.CurrentUserDTO

@Tag(name = "Current User")
interface CurrentUserApi {

    companion object {
        const val ENDPOINT_CURRENT_USER = "/currentUser"
    }

    @Operation(summary = "Get info about currently logged in user (if logged in)")
    @GetMapping(ENDPOINT_CURRENT_USER)
    fun getCurrentUser(): ResponseEntity<CurrentUserDTO>

}
