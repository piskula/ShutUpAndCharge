package sk.momosilabs.suac.server.dashboard.service.getMonthlySummary

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.security.service.CurrentUserService
import sk.momosilabs.suac.server.transaction.finished.model.MonthlyTransactionSummary
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence

@Service
open class GetMonthlySummary(
    private val currentUserService: CurrentUserService,
    private val transactionPersistence: TransactionFinishedPersistence,
) : GetMonthlySummaryUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun get(pageable: Pageable): Page<MonthlyTransactionSummary> =
        transactionPersistence.getMonthlySummaryByUserId(currentUserService.keycloakId(), pageable)

}
