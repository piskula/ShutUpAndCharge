package sk.momosilabs.suac.server.dashboard.persistence

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository
import sk.momosilabs.suac.server.dashboard.entity.ChargingOngoingEntity
import sk.momosilabs.suac.server.dashboard.persistence.repository.ChargingOngoingRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
open class ChargingOngoingPersistenceProvider(
    private val chargingOngoingRepository: ChargingOngoingRepository,
    private val accountRepository: AccountRepository,
): ChargingOngoingPersistence {

    @Transactional(readOnly = true)
    override fun getLastNotProcessedTrxNumber(): Int? =
        chargingOngoingRepository.findTopTrxNumberByOrderByIdDesc()?.trxNumber

    @Transactional
    override fun addOngoingTransaction(
        timestamp: Instant,
        accountId: Long,
        trxNumber: Int,
        trxIdentifier: String,
        energyMeter: Long
    ) {
        chargingOngoingRepository.save(
            ChargingOngoingEntity(
                id = 0L,
                trxNumber = trxNumber,
                trxIdentifier = trxIdentifier,
                energyMeter = energyMeter,
                timeStartUtc = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC),
                account = accountRepository.getReferenceById(accountId),
            )
        )
    }

    @Transactional(readOnly = true)
    override fun isChargingOfUserOngoing(trxIdentifier: String, accountId: Long): Boolean =
        chargingOngoingRepository.existsByTrxIdentifierAndAccountId(trxIdentifier, accountId)

}
