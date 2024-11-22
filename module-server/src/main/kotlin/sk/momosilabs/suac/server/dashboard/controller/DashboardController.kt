package sk.momosilabs.suac.server.dashboard.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.transaction.ChargingListDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.dashboard.DashboardApi
import sk.momosilabs.suac.server.transaction.controller.mapper.toDto
import sk.momosilabs.suac.server.transaction.model.ChargingListItem
import sk.momosilabs.suac.server.dashboard.service.getMyChargingList.GetMyChargingListUseCase
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel

@RestController
class DashboardController(
    private val getMyChargingList: GetMyChargingListUseCase,
) : DashboardApi {

    override fun getLastChargings(pageable: PageableDTO): PageDTO<ChargingListDTO> =
        getMyChargingList.get(pageable.toModel())
            .toDto(ChargingListItem::toDto)

}
