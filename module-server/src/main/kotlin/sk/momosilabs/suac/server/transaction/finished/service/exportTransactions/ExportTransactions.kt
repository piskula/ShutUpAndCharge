package sk.momosilabs.suac.server.transaction.finished.service.exportTransactions

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.common.model.GenericFile
import sk.momosilabs.suac.server.security.service.CurrentUserService
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence
import sk.momosilabs.suac.server.transaction.finished.service.export.ExcelExportService
import java.math.BigDecimal

@Service
open class ExportTransactions(
    private val currentUserService: CurrentUserService,
    private val transactionPersistence: TransactionFinishedPersistence,
    private val excelExportService: ExcelExportService,
) : ExportTransactionsUseCase {

    companion object {
        private val excelMapping = linkedMapOf<String, (TransactionFinished) -> Any?>(
            "Time (UTC)" to { it.time },
            "User" to { it.accountName },
            "Station" to { if (it.price > BigDecimal.ZERO) "Credit" else it.chargingStationId },
            "kWh" to { it.kwh },
            "Price" to { it.price },
        )
    }

    @IsUser
    @Transactional(readOnly = true)
    override fun export(filter: TransactionFinishedFilter, sort: Sort): GenericFile {
        val isAdmin = currentUserService.isAdmin()
        val transactions = transactionPersistence.getAllAsStream(
            filter = filter.addCurrentUserWhen(isNotAdmin = !isAdmin, currentUserService.keycloakId()),
            sort = sort,
        )
        val outputStream = transactions.use { excelExportService.exportEntityToStream(it, excelMapping) }
        return GenericFile.asExcelFile("transactions.xlsx", outputStream)
    }

    private fun TransactionFinishedFilter.addCurrentUserWhen(isNotAdmin: Boolean, currentUserId: String) =
        if (isNotAdmin) copy(accountIds = accountIds.plus(currentUserId)) else this

}
