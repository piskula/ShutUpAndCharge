package sk.momosilabs.suac.server.account.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.model.Account
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository

@Repository
open class AccountPersistenceProvider(
    private val accountRepository: AccountRepository,
): AccountPersistence {

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Account> =
        accountRepository.findAll(pageable).map { it.toModel() }

    @Transactional
    override fun setVerifiedForCharging(accountId: Long, verified: Boolean): Boolean {
        val account = accountRepository.findById(accountId).get()

        if (account.verifiedForCharging != verified)
            account.verifiedForCharging = verified

        return account.verifiedForCharging
    }

}
