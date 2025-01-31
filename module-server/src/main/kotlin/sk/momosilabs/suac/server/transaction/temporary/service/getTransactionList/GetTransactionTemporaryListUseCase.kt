package sk.momosilabs.suac.server.transaction.temporary.service.getTransactionList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporary

interface GetTransactionTemporaryListUseCase {

    fun get(pageable: Pageable): Page<TransactionTemporary>

}
