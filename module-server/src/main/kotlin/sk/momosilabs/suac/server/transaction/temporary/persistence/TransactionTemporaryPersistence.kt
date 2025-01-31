package sk.momosilabs.suac.server.transaction.temporary.persistence

import java.time.Instant

interface TransactionTemporaryPersistence {

    fun getLastNotProcessedTrxNumber(): Int?

    fun addOngoingTransaction(timestamp: Instant, accountId: Long, trxNumber: Int, trxIdentifier: String, energyMeter: Long)

    fun isChargingOfUserOngoing(trxIdentifier: String, accountId: Long): Boolean

}
