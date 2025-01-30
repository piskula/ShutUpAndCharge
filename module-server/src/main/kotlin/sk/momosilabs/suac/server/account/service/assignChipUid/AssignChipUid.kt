package sk.momosilabs.suac.server.account.service.assignChipUid

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.AccountPersistence
import sk.momosilabs.suac.server.common.IsAdmin

@Service
open class AssignChipUid(
    private val accountPersistence: AccountPersistence,
): AssignChipUidUseCase {

    @IsAdmin
    @Transactional
    override fun assignChipUid(accountId: Long, chipUid: String?) {
        accountPersistence.setAssignedChipUid(accountId, if (chipUid.isNullOrBlank()) null else chipUid)
    }

}
