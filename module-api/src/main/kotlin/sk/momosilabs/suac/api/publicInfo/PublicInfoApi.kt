package sk.momosilabs.suac.api.publicInfo

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.publicInfo.dto.ChargerStatusDTO
import sk.momosilabs.suac.api.publicInfo.dto.BuildInfoDTO

@Api("Public Info")
interface PublicInfoApi {

    companion object {
        const val ENDPOINT_INFO = "/info"
    }

    @ApiOperation("Check version")
    @GetMapping("$ENDPOINT_INFO/version")
    fun getVersion(request: HttpServletRequest): BuildInfoDTO

    @ApiOperation("Get Charging Status")
    @GetMapping("$ENDPOINT_INFO/chargerStatus", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChargerStatus(): ChargerStatusDTO

}
