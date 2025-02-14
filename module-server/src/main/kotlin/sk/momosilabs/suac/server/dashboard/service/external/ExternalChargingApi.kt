package sk.momosilabs.suac.server.dashboard.service.external

import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargerDataWrapper
import sk.momosilabs.suac.server.dashboard.model.charging.external.ExternalChargingLog
import java.time.LocalDateTime

interface ExternalChargingApi {

    fun getChargerStatus(): ExternalChargerDataWrapper

    fun startCharging(trxNumber: Int, identifier: String): ExternalChargerDataWrapper

    fun stopCharging(): ExternalChargerDataWrapper

    fun downloadTransactionsFromCloud(fromTimestampUtc: LocalDateTime): List<ExternalChargingLog>

}
