package sk.momosilabs.trucker.api.security

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import sk.momosilabs.trucker.api.security.dto.CurrentUserDTO

@Api("Current User")
interface CurrentUserApi {

    companion object {
        const val ENDPOINT_CURRENT_USER = "/currentUser"
    }

    @ApiOperation("Check version")
    @GetMapping(ENDPOINT_CURRENT_USER)
    fun getCurrentUser(): ResponseEntity<CurrentUserDTO>

    // used Post to invoke XSRF token, but token should already be known on first fetching of static sources (?)
    @ApiOperation("Secured endpoint post")
    @PostMapping("/api/secured", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun postSecured(@RequestBody thisIsBody: String): ResponseEntity<String>

    @ApiOperation("Secured endpoint get")
    @GetMapping("/api/secured")
    fun getSecured(): ResponseEntity<String>

}
