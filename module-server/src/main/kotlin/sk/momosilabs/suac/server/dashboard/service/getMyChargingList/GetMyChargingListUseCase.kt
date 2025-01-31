package sk.momosilabs.suac.server.dashboard.service.getMyChargingList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished

interface GetMyChargingListUseCase {

    fun get(pageable: Pageable): Page<TransactionFinished>

}
