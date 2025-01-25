package sk.momosilabs.suac.server.dashboard.service.getChargingStatus

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerStatusEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.OcppConnectorStatusEnum
import sk.momosilabs.suac.server.dashboard.service.external.ExternalChargingApi
import java.math.BigDecimal

@Service
open class GetChargingStatus(
    private val externalChargingApi: ExternalChargingApi,
): GetChargingStatusUseCase {

    private fun unknownStatus() = ChargerStatus(
        carState = CarStateEnum.UnknownOrError,
        modelStatus = ExternalChargerStatusEnum.NotChargingBecauseError,
        connectorStatusOcpp = OcppConnectorStatusEnum.Unavailable,
        occupiedFrom = null,
        chargedKwh = BigDecimal.ZERO,
    )

    @Transactional(readOnly = true)
    override fun getChargerStatus(): ChargerStatus {
        val statusResponse = externalChargingApi.getChargerStatus()
        return statusResponse.ifSuccess ?: unknownStatus()
    }

}
