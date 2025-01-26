package sk.momosilabs.suac.server.dashboard.service.external

import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerDataWrapper

interface ExternalChargingApi {

    fun getChargerStatus(): ExternalChargerDataWrapper

    fun startCharging(): ExternalChargerDataWrapper

    fun stopCharging(): ExternalChargerDataWrapper

}
