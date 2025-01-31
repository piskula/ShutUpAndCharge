package sk.momosilabs.suac.server.transaction.finished.service.getTransactionList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.security.service.CurrentUserService
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter

@Service
open class GetTransactionList(
    private val currentUserService: CurrentUserService,
    private val transactionPersistence: TransactionFinishedPersistence,
): GetTransactionListUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun get(filter: TransactionFinishedFilter, pageable: Pageable): Page<TransactionFinished> {
        val isAdmin = currentUserService.isAdmin()
        return transactionPersistence.getAll(
            filter = filter.addCurrentUserWhen(isNotAdmin = !isAdmin, currentUserService.userId()),
            pageable = pageable,
        )
    }

    private fun TransactionFinishedFilter.addCurrentUserWhen(isNotAdmin: Boolean, currentUserId: Long) =
        if (isNotAdmin)
            copy(accountIds = accountIds.plus(currentUserId))
        else
            this

}
