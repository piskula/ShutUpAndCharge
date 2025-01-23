package sk.momosilabs.suac.server.dashboard.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.dashboard.ChargingApi
import sk.momosilabs.suac.api.dashboard.dto.ChargerStatusDTO
import sk.momosilabs.suac.server.dashboard.controller.mapper.toDto
import sk.momosilabs.suac.server.dashboard.service.getChargingStatus.GetChargingStatusUseCase

@RestController
class ChargingController(
    private val getChargingStatus: GetChargingStatusUseCase,
) : ChargingApi {

    override fun getChargerStatus(): ChargerStatusDTO =
        getChargingStatus.getChargerStatus().toDto()

}
