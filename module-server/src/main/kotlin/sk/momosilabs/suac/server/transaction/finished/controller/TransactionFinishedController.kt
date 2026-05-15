package sk.momosilabs.suac.server.transaction.finished.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.finished.TransactionFinishedApi
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFilterDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFinishedDTO
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel
import sk.momosilabs.suac.server.transaction.finished.controller.mapper.toDto
import sk.momosilabs.suac.server.transaction.finished.controller.mapper.toModel
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.service.exportTransactions.ExportTransactionsUseCase
import sk.momosilabs.suac.server.transaction.finished.service.getTransactionList.GetTransactionListUseCase

@RestController
class TransactionFinishedController(
    private val getTransactionList: GetTransactionListUseCase,
    private val exportTransactions: ExportTransactionsUseCase,
) : TransactionFinishedApi {

    override fun getList(filter: TransactionFilterDTO, pageable: PageableDTO): PageDTO<TransactionFinishedDTO> =
        getTransactionList.get(filter.toModel(), pageable.toModel())
            .toDto(TransactionFinished::toDto)

    override fun exportTransactions(filter: TransactionFilterDTO, pageable: PageableDTO): ResponseEntity<ByteArray> {
        val bytes = exportTransactions.export(filter.toModel(), pageable.toModel())
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"transactions.xlsx\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(bytes)
    }

}
