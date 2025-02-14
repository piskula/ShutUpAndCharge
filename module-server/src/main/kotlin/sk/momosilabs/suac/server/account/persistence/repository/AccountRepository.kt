package sk.momosilabs.suac.server.account.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.account.entity.AccountEntity

@Repository
interface AccountRepository: JpaRepository<AccountEntity, Long> {

    fun findByIdKeycloak(idKeycloak: String): AccountEntity?

    fun findByAssignedChipUid(assignedChipUid: String): AccountEntity?

    fun findByAssignedChipUidIn(assignedChipUids: Set<String>): List<AccountEntity>

}
