package sk.momosilabs.suac.server.transaction.finished.service.getTransactionList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter

interface GetTransactionListUseCase {

    fun get(filter: TransactionFinishedFilter, pageable: Pageable): Page<TransactionFinished>

}
