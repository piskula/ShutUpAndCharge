package sk.momosilabs.trucker.api.security

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.trucker.api.security.dto.CurrentUserDTO

@Api("Current User")
interface CurrentUserApi {

    companion object {
        const val ENDPOINT_CURRENT_USER = "/api/currentUser"
    }

    @ApiOperation("Check version")
    @GetMapping(ENDPOINT_CURRENT_USER)
    fun get(): ResponseEntity<CurrentUserDTO>

}
