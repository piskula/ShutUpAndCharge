package sk.momosilabs.trucker.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.trucker.api.model.buildInfo.BuildInfoDTO

@Api("Build info & version")
interface BuildInfoApi {

    companion object {
        const val ENDPOINT_API_VERSION = "/api/version"
    }

    @ApiOperation("Check version")
    @GetMapping(ENDPOINT_API_VERSION)
    fun get(): BuildInfoDTO

}
