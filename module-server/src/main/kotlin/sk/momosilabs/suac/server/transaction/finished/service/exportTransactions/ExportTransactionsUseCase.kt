package sk.momosilabs.suac.server.transaction.finished.service.exportTransactions

import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.common.model.GenericFile
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter

interface ExportTransactionsUseCase {

    fun export(filter: TransactionFinishedFilter, pageable: Pageable): GenericFile

}
