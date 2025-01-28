package sk.momosilabs.suac.server.dashboard.persistence

import java.time.Instant

interface ChargingOngoingPersistence {

    fun getLastNotProcessedTrxNumber(): Int?

    fun addOngoingTransaction(timestamp: Instant, accountId: Long, trxNumber: Int, trxIdentifier: String, energyMeter: Long)

    fun isChargingOfUserOngoing(trxIdentifier: String, accountId: Long): Boolean

}
