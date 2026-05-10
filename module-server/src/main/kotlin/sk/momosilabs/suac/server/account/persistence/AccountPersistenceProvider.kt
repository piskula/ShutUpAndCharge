package sk.momosilabs.suac.server.account.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.model.Account
import sk.momosilabs.suac.server.account.persistence.repository.AccountRepository
import sk.momosilabs.suac.server.common.GlobalNotFoundException

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

    @Transactional
    override fun setAssignedChipUid(accountId: Long, chipUid: String?): String? {
        val account = accountRepository.findById(accountId).get()
        account.assignedChipUid = chipUid
        return account.assignedChipUid
    }

    @Transactional(readOnly = true)
    override fun canCharge(accountId: String): Boolean =
        accountRepository.findByIdKeycloak(accountId)?.verifiedForCharging
            ?: throw GlobalNotFoundException("accountId=$accountId not found")

    @Transactional(readOnly = true)
    override fun findUserIdByChipUid(chipUid: String): String? =
        accountRepository.findByAssignedChipUid(assignedChipUid = chipUid)?.idKeycloak

    @Transactional(readOnly = true)
    override fun getChipUidToUserIdMap(chipUids: Set<String>): Map<String, Long> =
        accountRepository.findByAssignedChipUidIn(chipUids).associateBy({ it.assignedChipUid!! }, { it.id })

}
