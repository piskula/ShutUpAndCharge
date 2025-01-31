package sk.momosilabs.suac.api.transaction.finished

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableApiParam
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.finished.dto.ChargingListDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFilterDTO

@Api("Finished Transaction")
interface TransactionFinishedApi {

    companion object {
        private const val ENDPOINT_TRANSACTION_FINISHED = "/api/transaction/finished"
    }

    @ApiOperation("Get list of transactions")
    @PageableApiParam
    @PostMapping(
        ENDPOINT_TRANSACTION_FINISHED,
        produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getList(@RequestBody filter: TransactionFilterDTO, pageable: PageableDTO): PageDTO<ChargingListDTO>

}
