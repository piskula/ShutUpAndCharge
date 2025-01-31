package sk.momosilabs.suac.server.info.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.publicInfo.dto.ChargerStatusDTO
import sk.momosilabs.suac.api.publicInfo.PublicInfoApi
import sk.momosilabs.suac.api.publicInfo.dto.BuildInfoDTO
import sk.momosilabs.suac.server.info.controller.mapper.toDto
import sk.momosilabs.suac.server.dashboard.service.getChargingStatus.GetChargingStatusUseCase
import java.time.ZoneOffset

@RestController
class InfoController(
    private val buildProperties: BuildProperties,
    private val getChargingStatus: GetChargingStatusUseCase,
) : PublicInfoApi {

    override fun getVersion(request: HttpServletRequest): BuildInfoDTO = BuildInfoDTO(
        name = buildProperties.name,
        version = buildProperties.version,
        time = buildProperties.time.atOffset(ZoneOffset.UTC).toZonedDateTime(),
        url = request.requestURL.toString(),
    )

    override fun getChargerStatus(): ChargerStatusDTO =
        getChargingStatus.getChargerStatus().toDto()

}
