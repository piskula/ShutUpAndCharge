package sk.momosilabs.suac.server.dashboard.service.startCharging

import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus

interface StartChargingUseCase {

    fun startCharging(): ChargerStatus

}
