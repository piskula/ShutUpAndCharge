package sk.momosilabs.suac.api.dashboard

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFinishedDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableApiParam
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.publicInfo.dto.ChargerStatusDTO

@Api("Dashboard")
interface DashboardApi {

    companion object {
        private const val ENDPOINT_DASHBOARD = "/api/dashboard"
    }

    @ApiOperation("Get Last Chargings")
    @PageableApiParam
    @GetMapping("$ENDPOINT_DASHBOARD/lastTransactions", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLastChargings(pageable: PageableDTO): PageDTO<TransactionFinishedDTO>

    @ApiOperation("Start Charging")
    @PostMapping("$ENDPOINT_DASHBOARD/start", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun startCharging(): ChargerStatusDTO

    @ApiOperation("Stop Charging")
    @PostMapping("$ENDPOINT_DASHBOARD/stop", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun stopCharging(): ChargerStatusDTO

}
