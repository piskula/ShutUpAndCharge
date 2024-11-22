package sk.momosilabs.suac.server.dashboard.service.getMyChargingList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.model.ChargingListItem

interface GetMyChargingListUseCase {

    fun get(pageable: Pageable): Page<ChargingListItem>

}
