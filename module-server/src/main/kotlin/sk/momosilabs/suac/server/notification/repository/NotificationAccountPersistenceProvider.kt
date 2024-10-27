package sk.momosilabs.suac.server.notification.repository

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.entity.AccountRole
import sk.momosilabs.suac.server.account.entity.TransportCompanyDriverEntity
import sk.momosilabs.suac.server.account.entity.TransportCompanyManagerEntity
import sk.momosilabs.suac.server.account.repository.AccountRepository
import sk.momosilabs.suac.server.account.repository.CompanyDriverRepository
import sk.momosilabs.suac.server.account.repository.CompanyManagerRepository
import sk.momosilabs.suac.server.notification.service.NotificationAccountPersistence

@Service
open class NotificationAccountPersistenceProvider(
    private val accountRepository: AccountRepository,
    private val companyManagerRepository: CompanyManagerRepository,
    private val companyDriverRepository: CompanyDriverRepository,
): NotificationAccountPersistence {

    @Transactional(readOnly = true)
    override fun isAdmin(userId: String): Boolean =
        accountRepository.findByIdKeycloak(userId)!!.role == AccountRole.Admin

    @Transactional(readOnly = true)
    override fun getAssignedCompanyIds(userId: String): Set<Long> {
        val companyIdsFromDrivers = companyDriverRepository.findAllByDriverIdKeycloak(userId)
            .toDriversCompanyIds()
        val companyIdsFromManagers = companyManagerRepository.findAllByAdminIdKeycloak(userId)
            .toManagersCompanyIds()

        return companyIdsFromDrivers union companyIdsFromManagers
    }

    private fun Iterable<TransportCompanyDriverEntity>.toDriversCompanyIds(): Set<Long> =
        mapTo(HashSet()) { it.company.id }
    private fun Iterable<TransportCompanyManagerEntity>.toManagersCompanyIds(): Set<Long> =
        mapTo(HashSet()) { it.company.id }

}
