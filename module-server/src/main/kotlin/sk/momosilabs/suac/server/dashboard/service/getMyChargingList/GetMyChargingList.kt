package sk.momosilabs.suac.server.dashboard.service.getMyChargingList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.transaction.persistence.TransactionFinishedPersistence
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.security.service.CurrentUserService

@Service
open class GetMyChargingList(
    private val currentUserService: CurrentUserService,
    private val transactionPersistence: TransactionFinishedPersistence,
): GetMyChargingListUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun get(pageable: Pageable): Page<ChargingListItem> =
        transactionPersistence.getNegativeByUserId(currentUserService.userId(), pageable)

}
