package sk.momosilabs.suac.server.account.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.model.Account
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import sk.momosilabs.suac.server.charging.persistence.repository.ChargingFinishedRepository

@Repository
open class AccountPersistenceProvider(
    private val accountRepository: AccountRepository,
): AccountPersistence {

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Account> =
        accountRepository.findAll(pageable).map { it.toModel() }

}
