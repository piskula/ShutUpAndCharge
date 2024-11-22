package sk.momosilabs.suac.server.transaction.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.transaction.model.ChargingToCreate
import sk.momosilabs.suac.server.transaction.persistence.repository.ChargingFinishedRepository
import java.math.BigDecimal

@Repository
open class TransactionFinishedPersistenceProvider(
    private val accountRepository: AccountRepository,
    private val chargingRepository: ChargingFinishedRepository,
): TransactionFinishedPersistence {

    @Transactional(readOnly = true)
    override fun getAll(filterByUserId: Long?, pageable: Pageable): Page<ChargingListItem> =
        chargingRepository.findAllByAccountId(filterByUserId, pageable).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getNegativeByUserId(userId: Long, pageable: Pageable): Page<ChargingListItem> =
        chargingRepository.findAllByAccountIdAndPriceLessThan(userId, BigDecimal.ZERO, pageable).map { it.toModel() }

    @Transactional
    override fun saveFinishedCharging(charging: ChargingToCreate, userId: Long): ChargingListItem {
        val account = accountRepository.getReferenceById(userId)
        val chargingEntity = charging.asNewEntity(account)
        return chargingRepository.save(chargingEntity).toModel()
    }

}
