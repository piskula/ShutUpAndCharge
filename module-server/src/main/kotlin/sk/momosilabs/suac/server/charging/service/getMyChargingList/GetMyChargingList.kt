package sk.momosilabs.suac.server.charging.service.getMyChargingList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import sk.momosilabs.suac.server.charging.persistence.ChargingPersistence
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.security.service.CurrentUserService

@Service
open class GetMyChargingList(
    private val currentUserService: CurrentUserService,
    private val chargingPersistence: ChargingPersistence,
): GetMyChargingListUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun get(pageable: Pageable): Page<ChargingListItem> {
        val userId = currentUserService.userId()
        return chargingPersistence.getByUserId(userId, pageable)
    }

}
