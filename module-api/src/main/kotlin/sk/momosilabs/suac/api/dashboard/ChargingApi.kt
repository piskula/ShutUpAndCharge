package sk.momosilabs.suac.api.dashboard

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.dashboard.dto.ChargerStatusDTO

@Api("Charging")
interface ChargingApi {

    companion object {
        private const val ENDPOINT_CHARGING = "/info/charging"
    }

    @ApiOperation("Get Charging Status")
    @GetMapping(ENDPOINT_CHARGING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChargerStatus(): ChargerStatusDTO

}
