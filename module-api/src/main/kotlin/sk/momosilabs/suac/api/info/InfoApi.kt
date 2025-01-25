package sk.momosilabs.suac.api.info

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.dashboard.dto.ChargerStatusDTO
import sk.momosilabs.suac.api.info.dto.BuildInfoDTO

@Api("Info Public")
interface InfoApi {

    companion object {
        const val ENDPOINT_INFO = "/info"
    }

    @ApiOperation("Check version")
    @GetMapping("$ENDPOINT_INFO/version")
    fun get(request: HttpServletRequest): BuildInfoDTO

    @ApiOperation("Get Charging Status")
    @GetMapping("$ENDPOINT_INFO/charging", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChargerStatus(): ChargerStatusDTO

}
