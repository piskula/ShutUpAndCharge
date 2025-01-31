package sk.momosilabs.suac.server.transaction.temporary.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.temporary.TransactionTemporaryApi
import sk.momosilabs.suac.api.transaction.temporary.dto.TransactionTemporaryDTO
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel
import sk.momosilabs.suac.server.transaction.temporary.controller.mapper.toDto
import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporary
import sk.momosilabs.suac.server.transaction.temporary.service.getTransactionList.GetTransactionTemporaryListUseCase

@RestController
class TransactionTemporaryController(
    private val getTransactionTemporaryList: GetTransactionTemporaryListUseCase,
) : TransactionTemporaryApi {

    override fun getList(pageable: PageableDTO): PageDTO<TransactionTemporaryDTO> =
        getTransactionTemporaryList.get(pageable.toModel())
            .toDto(TransactionTemporary::toDto)

}
