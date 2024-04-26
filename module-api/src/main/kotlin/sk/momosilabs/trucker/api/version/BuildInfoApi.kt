package sk.momosilabs.trucker.api.version

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.trucker.api.version.dto.BuildInfoDTO

@Api("Build Info")
interface BuildInfoApi {

    companion object {
        const val ENDPOINT_VERSION = "/info/version"
    }

    @ApiOperation("Check version")
    @GetMapping(ENDPOINT_VERSION)
    fun get(): BuildInfoDTO

}
