package sk.momosilabs.suac.api.transaction

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableApiParam
import sk.momosilabs.suac.api.common.dto.PageableDTO
import java.math.BigDecimal

@Api("Transaction")
interface TransactionApi {

    companion object {
        private const val ENDPOINT_CHARGING = "/api/transaction"
    }

    @ApiOperation("Get list of transactions")
    @PageableApiParam
    @PostMapping(ENDPOINT_CHARGING,
        produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getList(@RequestBody filter: TransactionFilterDTO, pageable: PageableDTO): PageDTO<ChargingListDTO>

    @PostMapping("${ENDPOINT_CHARGING}/topUp/{accountId}",
        produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun topUpAccount(@PathVariable accountId: Long, @RequestBody amount: BigDecimal): BigDecimal

}
