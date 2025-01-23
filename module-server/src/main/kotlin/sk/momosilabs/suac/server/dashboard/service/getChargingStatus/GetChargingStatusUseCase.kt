package sk.momosilabs.suac.server.dashboard.service.getChargingStatus

import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus

interface GetChargingStatusUseCase {

    fun getChargerStatus(): ChargerStatus

}
