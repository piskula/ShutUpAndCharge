package sk.momosilabs.suac.server.charging.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import sk.momosilabs.suac.server.charging.persistence.repository.ChargingFinishedRepository

@Repository
open class ChargingPersistenceProvider(
    private val accountRepository: AccountRepository,
    private val chargingRepository: ChargingFinishedRepository,
): ChargingPersistence {

    @Transactional(readOnly = true)
    override fun getByUserId(userId: Long, pageable: Pageable): Page<ChargingListItem> =
        chargingRepository.findAllByAccountId(userId, pageable).map { it.toModel() }

    @Transactional
    override fun saveFinishedCharging(charging: ChargingListItem, userId: Long): ChargingListItem {
        val account = accountRepository.getReferenceById(userId)
        val chargingEntity = charging.asNewEntity(account)
        return chargingRepository.save(chargingEntity).toModel()
    }

}
