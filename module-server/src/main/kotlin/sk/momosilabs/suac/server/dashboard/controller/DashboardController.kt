package sk.momosilabs.suac.server.dashboard.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFinishedDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.dashboard.DashboardApi
import sk.momosilabs.suac.api.publicInfo.dto.ChargerStatusDTO
import sk.momosilabs.suac.server.transaction.finished.controller.mapper.toDto
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.dashboard.service.getMyChargingList.GetMyChargingListUseCase
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel
import sk.momosilabs.suac.server.dashboard.service.getMyBalance.GetMyBalanceUseCase
import sk.momosilabs.suac.server.dashboard.service.startCharging.StartChargingUseCase
import sk.momosilabs.suac.server.dashboard.service.stopCharging.StopChargingUseCase
import sk.momosilabs.suac.server.info.controller.mapper.toDto
import java.math.BigDecimal

@RestController
class DashboardController(
    private val getMyChargingList: GetMyChargingListUseCase,
    private val getMyBalance: GetMyBalanceUseCase,
    private val startCharging: StartChargingUseCase,
    private val stopCharging: StopChargingUseCase,
) : DashboardApi {

    override fun getLastChargings(pageable: PageableDTO): PageDTO<TransactionFinishedDTO> =
        getMyChargingList.get(pageable.toModel())
            .toDto(TransactionFinished::toDto)

    override fun getMyBalance(): BigDecimal =
        getMyBalance.getCurrentBalance()

    override fun startCharging(): ChargerStatusDTO =
        startCharging.startCharging().toDto()

    override fun stopCharging(): ChargerStatusDTO =
        stopCharging.stopCharging().toDto()

}
