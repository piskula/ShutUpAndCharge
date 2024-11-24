package sk.momosilabs.suac.api.dashboard

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.transaction.ChargingListDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableApiParam
import sk.momosilabs.suac.api.common.dto.PageableDTO

@Api("Dashboard")
interface DashboardApi {

    companion object {
        private const val ENDPOINT_DASHBOARD = "/api/dashboard"
    }

    @ApiOperation("Get Last Chargings")
    @PageableApiParam
    @GetMapping(ENDPOINT_DASHBOARD, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLastChargings(pageable: PageableDTO): PageDTO<ChargingListDTO>

}
