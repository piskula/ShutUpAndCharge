package sk.momosilabs.suac.server.transaction.finished.service.exportTransactions

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.security.service.CurrentUserService
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence
import sk.momosilabs.suac.server.transaction.finished.service.export.ExcelExportService

@Service
open class ExportTransactions(
    private val currentUserService: CurrentUserService,
    private val transactionPersistence: TransactionFinishedPersistence,
    private val excelExportService: ExcelExportService,
) : ExportTransactionsUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun export(filter: TransactionFinishedFilter, pageable: Pageable): ByteArray {
        val isAdmin = currentUserService.isAdmin()
        val transactions = transactionPersistence.getAll(
            filter = filter.addCurrentUserWhen(isNotAdmin = !isAdmin, currentUserService.keycloakId()),
            pageable = pageable,
        )
        return excelExportService.exportTransactions(transactions.content)
    }

    private fun TransactionFinishedFilter.addCurrentUserWhen(isNotAdmin: Boolean, currentUserId: String) =
        if (isNotAdmin) copy(accountIds = accountIds.plus(currentUserId)) else this

}
