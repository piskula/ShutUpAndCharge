package sk.momosilabs.suac.server.transaction.temporary.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository
import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporary
import sk.momosilabs.suac.server.transaction.temporary.persistence.entity.ChargingOngoingEntity
import sk.momosilabs.suac.server.transaction.temporary.persistence.repository.TransactionTemporaryRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
open class TransactionTemporaryPersistenceProvider(
    private val transactionRepository: TransactionTemporaryRepository,
    private val accountRepository: AccountRepository,
): TransactionTemporaryPersistence {

    @Transactional(readOnly = true)
    override fun getAll(pageable: Pageable): Page<TransactionTemporary> =
        transactionRepository.findAll(pageable).map(ChargingOngoingEntity::toModel)

    @Transactional(readOnly = true)
    override fun getLastNotProcessedTrxNumber(): Int? =
        transactionRepository.findTopTrxNumberByOrderByIdDesc()?.trxNumber

    @Transactional
    override fun addOngoingTransaction(
        timestamp: Instant,
        accountId: Long,
        trxNumber: Int,
        trxIdentifier: String,
        energyMeter: Long
    ) {
        transactionRepository.save(
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
        transactionRepository.existsByTrxIdentifierAndAccountId(trxIdentifier, accountId)

}
