package sk.momosilabs.suac.api.dashboard

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.dashboard.dto.MonthlyTransactionSummaryDTO
import sk.momosilabs.suac.api.publicInfo.dto.ChargerStatusDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFinishedDTO
import java.math.BigDecimal

@Tag(name = "Dashboard")
interface DashboardApi {

    companion object {
        private const val ENDPOINT_DASHBOARD = "/api/dashboard"
    }

    @Operation(summary = "Get My Last Chargings")
    @GetMapping("$ENDPOINT_DASHBOARD/lastTransactions", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLastChargings(@ParameterObject pageable: PageableDTO): PageDTO<TransactionFinishedDTO>

    @Operation(summary = "Get My Monthly Transaction Summary")
    @GetMapping("$ENDPOINT_DASHBOARD/monthlySummary", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMonthlySummary(@ParameterObject pageable: PageableDTO): PageDTO<MonthlyTransactionSummaryDTO>

    @Operation(summary = "Get My Account Balance")
    @GetMapping("$ENDPOINT_DASHBOARD/balance", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMyBalance(): BigDecimal

    @Operation(summary = "Start Charging")
    @PostMapping("$ENDPOINT_DASHBOARD/start", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun startCharging(): ChargerStatusDTO

    @Operation(summary = "Set charging current")
    @PostMapping("$ENDPOINT_DASHBOARD/setChargingParams", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun setChargingParams(@RequestParam current: Int): Int

    @Operation(summary = "Stop Charging")
    @PostMapping("$ENDPOINT_DASHBOARD/stop", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun stopCharging(): ChargerStatusDTO

}
