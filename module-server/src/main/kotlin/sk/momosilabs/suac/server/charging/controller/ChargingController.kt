package sk.momosilabs.suac.server.charging.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.charging.ChargingApi
import sk.momosilabs.suac.api.charging.ChargingListDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.server.charging.controller.mapper.toDto
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import sk.momosilabs.suac.server.charging.service.getMyChargingList.GetMyChargingListUseCase
import sk.momosilabs.suac.server.charging.service.topUpAccount.TopUpAccountUseCase
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel
import java.math.BigDecimal

@RestController
class ChargingController(
    private val getMyChargingList: GetMyChargingListUseCase,
    private val topUpAccount: TopUpAccountUseCase,
) : ChargingApi {

    override fun getChargingList(pageable: PageableDTO): PageDTO<ChargingListDTO> =
        getMyChargingList.get(pageable.toModel())
            .toDto(ChargingListItem::toDto)

    override fun updateVerifiedFlag(accountId: Long, amount: BigDecimal): BigDecimal =
        topUpAccount.topUp(accountId, amount)

}
