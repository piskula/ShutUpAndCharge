package sk.momosilabs.suac.server.transaction.service.getTransactionList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.api.security.dto.CurrentUserRoleDTO
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.transaction.persistence.TransactionFinishedPersistence
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.security.service.CurrentUserService

@Service
open class GetTransactionList(
    private val currentUserService: CurrentUserService,
    private val transactionPersistence: TransactionFinishedPersistence,
): GetTransactionListUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun get(pageable: Pageable): Page<ChargingListItem> {
        val isAdmin = currentUserService.getCurrentUser().momoRoles().contains(CurrentUserRoleDTO.Admin)
        return transactionPersistence.getAll(
            filterByUserId = if (isAdmin) null else currentUserService.userId(),
            pageable = pageable,
        )
    }

}
