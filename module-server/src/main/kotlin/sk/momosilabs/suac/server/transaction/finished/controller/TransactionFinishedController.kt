package sk.momosilabs.suac.server.transaction.finished.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.transaction.finished.TransactionFinishedApi
import sk.momosilabs.suac.api.transaction.finished.dto.ChargingListDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFilterDTO
import sk.momosilabs.suac.server.transaction.finished.controller.mapper.toDto
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel
import sk.momosilabs.suac.server.transaction.finished.controller.mapper.toModel
import sk.momosilabs.suac.server.transaction.finished.service.getTransactionList.GetTransactionListUseCase

@RestController
class TransactionFinishedController(
    private val getTransactionList: GetTransactionListUseCase,
) : TransactionFinishedApi {

    override fun getList(filter: TransactionFilterDTO, pageable: PageableDTO): PageDTO<ChargingListDTO> =
        getTransactionList.get(filter.toModel(), pageable.toModel())
            .toDto(TransactionFinished::toDto)

}
