package sk.momosilabs.suac.server.dashboard.service.getMonthlySummary

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.transaction.finished.model.MonthlyTransactionSummary

interface GetMonthlySummaryUseCase {

    fun get(pageable: Pageable): Page<MonthlyTransactionSummary>

}
