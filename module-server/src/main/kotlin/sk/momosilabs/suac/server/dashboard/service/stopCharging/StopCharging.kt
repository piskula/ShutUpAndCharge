package sk.momosilabs.suac.server.dashboard.service.stopCharging

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus

@Service
open class StopCharging(
): StopChargingUseCase {

    @IsUser
    @Transactional
    override fun stopCharging(): ChargerStatus {
        TODO("Not yet implemented")
    }

}
