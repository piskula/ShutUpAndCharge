package sk.momosilabs.suac.server.transaction.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.transaction.TransactionApi
import sk.momosilabs.suac.api.transaction.ChargingListDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.TransactionFilterDTO
import sk.momosilabs.suac.server.transaction.controller.mapper.toDto
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.transaction.service.topUpAccount.TopUpAccountUseCase
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel
import sk.momosilabs.suac.server.transaction.controller.mapper.toModel
import sk.momosilabs.suac.server.transaction.service.getTransactionList.GetTransactionListUseCase
import java.math.BigDecimal

@RestController
class TransactionController(
    private val getTransactionList: GetTransactionListUseCase,
    private val topUpAccount: TopUpAccountUseCase,
) : TransactionApi {

    override fun getList(filter: TransactionFilterDTO, pageable: PageableDTO): PageDTO<ChargingListDTO> =
        getTransactionList.get(filter.toModel(), pageable.toModel())
            .toDto(ChargingListItem::toDto)

    override fun topUpAccount(accountId: Long, amount: BigDecimal): BigDecimal =
        topUpAccount.topUp(accountId, amount)

}
