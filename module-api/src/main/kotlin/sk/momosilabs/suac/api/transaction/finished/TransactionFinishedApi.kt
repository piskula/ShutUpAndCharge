package sk.momosilabs.suac.api.transaction.finished

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFinishedDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFilterDTO

@Tag(name = "Finished Transaction")
interface TransactionFinishedApi {

    companion object {
        private const val ENDPOINT_TRANSACTION_FINISHED = "/api/transaction/finished"
    }

    @Operation(summary = "Get list of transactions")
    @PostMapping(
        ENDPOINT_TRANSACTION_FINISHED,
        produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getList(@RequestBody filter: TransactionFilterDTO, pageable: PageableDTO): PageDTO<TransactionFinishedDTO>

}
