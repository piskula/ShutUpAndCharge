package sk.momosilabs.suac.api.version

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.version.dto.BuildInfoDTO

@Api("Build Info")
interface BuildInfoApi {

    companion object {
        const val ENDPOINT_VERSION = "/info/version"
    }

    @ApiOperation("Check version")
    @GetMapping(ENDPOINT_VERSION)
    fun get(request: HttpServletRequest): BuildInfoDTO

}
