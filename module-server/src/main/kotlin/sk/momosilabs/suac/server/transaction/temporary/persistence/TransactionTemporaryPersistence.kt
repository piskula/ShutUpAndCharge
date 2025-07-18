package sk.momosilabs.suac.server.transaction.temporary.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporary
import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporaryToMatch
import java.time.Instant

interface TransactionTemporaryPersistence {

    fun getAll(pageable: Pageable): Page<TransactionTemporary>

    fun getById(trxIdentifier: String): TransactionTemporary?

    fun getLastNotProcessedTrxNumber(): Int?

    fun addOngoingTransaction(timestamp: Instant, accountId: Long, trxNumber: Int, trxIdentifier: String, energyMeter: Long)

    fun isChargingOfUserOngoing(trxIdentifier: String, accountId: Long): Boolean

    fun fetchAwaitingTransactionsForStation(stationId: String): List<TransactionTemporaryToMatch>

    fun deleteAwaitingTransactionsForStation(stationId: String, energyMeterValues: Set<Long>)

}
