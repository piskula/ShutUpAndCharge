package sk.momosilabs.suac.server.transaction.service.getTransactionList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.model.ChargingListItem

interface GetTransactionListUseCase {

    fun get(pageable: Pageable): Page<ChargingListItem>

}
