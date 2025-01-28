package sk.momosilabs.suac.server.dashboard.service.getChargingStatus

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerStatusEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.ForceStateEnum
import sk.momosilabs.suac.server.dashboard.model.charging.external.OcppConnectorStatusEnum
import sk.momosilabs.suac.server.dashboard.service.external.ExternalChargingApi
import java.math.BigDecimal

@Service
open class GetChargingStatus(
    private val externalChargingApi: ExternalChargingApi,
): GetChargingStatusUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(GetChargingStatus::class.java)

        val unknownStatus = ChargerStatus(
            carState = CarStateEnum.UnknownOrError,
            modelStatus = ExternalChargerStatusEnum.NotChargingBecauseError,
            connectorStatusOcpp = OcppConnectorStatusEnum.Unavailable,
            forceState = ForceStateEnum.Neutral,
            occupiedFrom = null,
            chargedKwh = BigDecimal.ZERO,
            trxNumber = null,
            meterEnergyTotal = 0L,
            customIdentifier = "",
            rfidUid = null,
        )
    }

    @Transactional(readOnly = true)
    override fun getChargerStatus(): ChargerStatus {
        val statusResponse = externalChargingApi.getChargerStatus()
        return statusResponse.ifSuccess
            ?: unknownStatus.also {
                logger.debug("Error while fetching charger status: " + statusResponse.ifError?.toString())
            }
    }

}
