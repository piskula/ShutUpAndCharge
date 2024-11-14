package sk.momosilabs.suac.api.charging

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import java.math.BigDecimal

@Api("Charging")
interface ChargingApi {

    companion object {
        private const val ENDPOINT_CHARGING = "/api/charging"
    }

    @ApiOperation("Check version")
    // TODO check if can be extracted to custom annotation
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer", dataTypeClass = Integer::class),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer", dataTypeClass = Integer::class),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", dataTypeClass = String::class)
    )
    @GetMapping(ENDPOINT_CHARGING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChargingList(pageable: PageableDTO): PageDTO<ChargingListDTO>

    @PostMapping("${ENDPOINT_CHARGING}/topUp/{accountId}",
        produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun topUpAccount(@PathVariable accountId: Long, @RequestBody amount: BigDecimal): BigDecimal

}
