package sk.momosilabs.suac.server.transaction.finished.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.finished.TransactionFinishedApi
import sk.momosilabs.suac.api.transaction.finished.dto.MonthlyKwhChartDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFilterDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFinishedDTO
import sk.momosilabs.suac.server.common.controller.mapper.mapToResponseEntity
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel
import sk.momosilabs.suac.server.common.toSortModel
import sk.momosilabs.suac.server.transaction.finished.controller.mapper.toDto
import sk.momosilabs.suac.server.transaction.finished.controller.mapper.toModel
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.service.exportTransactions.ExportTransactionsUseCase
import sk.momosilabs.suac.server.transaction.finished.service.getMonthlyKwh.GetMonthlyKwhUseCase
import sk.momosilabs.suac.server.transaction.finished.service.getTransactionList.GetTransactionListUseCase

@RestController
class TransactionFinishedController(
    private val getTransactionList: GetTransactionListUseCase,
    private val exportTransactions: ExportTransactionsUseCase,
    private val getMonthlyKwh: GetMonthlyKwhUseCase,
) : TransactionFinishedApi {

    override fun getList(filter: TransactionFilterDTO, pageable: PageableDTO): PageDTO<TransactionFinishedDTO> =
        getTransactionList.get(filter.toModel(), pageable.toModel())
            .toDto(TransactionFinished::toDto)

    override fun exportTransactions(filter: TransactionFilterDTO, sort: String?) =
        exportTransactions.export(filter.toModel(), sort.toSortModel()).mapToResponseEntity()

    override fun getMonthlyKwh(filter: TransactionFilterDTO): MonthlyKwhChartDTO =
        getMonthlyKwh.get(filter.toModel()).toDto()

}
