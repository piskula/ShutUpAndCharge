package sk.momosilabs.suac.server.dashboard.service.stopCharging

import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus

interface StopChargingUseCase {

    fun stopCharging(): ChargerStatus

}
