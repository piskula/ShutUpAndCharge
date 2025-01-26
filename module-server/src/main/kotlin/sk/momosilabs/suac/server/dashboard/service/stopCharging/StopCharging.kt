package sk.momosilabs.suac.server.dashboard.service.stopCharging

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum
import sk.momosilabs.suac.server.dashboard.service.external.ExternalChargingApi
import sk.momosilabs.suac.server.dashboard.service.getChargingStatus.GetChargingStatus.Companion.unknownStatus

@Service
open class StopCharging(
    private val externalChargingApi: ExternalChargingApi,
): StopChargingUseCase {

    @IsUser
    @Transactional
    override fun stopCharging(): ChargerStatus {
        // TODO check if charging belong to this user
        val carState = externalChargingApi.getChargerStatus().ifSuccess?.carState
        if (carState != CarStateEnum.Charging)
            throw IllegalArgumentException("charging is not ongoing, but $carState")

        return externalChargingApi.stopCharging().ifSuccess ?: unknownStatus
    }

}
