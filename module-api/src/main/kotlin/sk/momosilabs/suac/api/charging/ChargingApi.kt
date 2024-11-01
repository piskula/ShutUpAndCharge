package sk.momosilabs.suac.api.charging

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO

@Api("Charging")
interface ChargingApi {

    companion object {
        const val ENDPOINT_CHARGING = "/api/charging"
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

}
