package sk.momosilabs.suac.server.account.service.getUserList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.model.Account
import sk.momosilabs.suac.server.account.persistence.AccountPersistence
import sk.momosilabs.suac.server.common.IsAdmin
import sk.momosilabs.suac.server.common.mapToSet
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence
import java.math.BigDecimal

@Service
open class GetUserList(
    private val accountPersistence: AccountPersistence,
    private val transactionFinishedPersistence: TransactionFinishedPersistence,
): GetUserListUseCase {

    @IsAdmin
    @Transactional(readOnly = true)
    override fun get(pageable: Pageable): Page<Account> {
        val users = accountPersistence.findAll(pageable)
        val userIds = users.content.mapToSet { it.id }

        val balancePerUser = transactionFinishedPersistence.sumUpForUsers(userIds)

        return users.onEach { user ->
            user.balance = balancePerUser[user.id] ?: BigDecimal.ZERO
        }
    }

}
