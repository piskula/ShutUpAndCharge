package sk.momosilabs.suac.server.dashboard.service.startCharging

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus

@Service
open class StartCharging(
): StartChargingUseCase {

    @IsUser
    @Transactional
    override fun startCharging(): ChargerStatus {
        TODO("Not yet implemented")
    }

}
