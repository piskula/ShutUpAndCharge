package sk.momosilabs.suac.server.transaction.service.getTransactionList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.transaction.model.TransactionFilter

interface GetTransactionListUseCase {

    fun get(filter: TransactionFilter, pageable: Pageable): Page<ChargingListItem>

}
