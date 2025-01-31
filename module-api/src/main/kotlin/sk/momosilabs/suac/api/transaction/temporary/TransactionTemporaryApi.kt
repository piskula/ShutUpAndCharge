package sk.momosilabs.suac.api.transaction.temporary

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableApiParam
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.temporary.dto.TransactionTemporaryDTO

@Api("Temporary Transaction")
interface TransactionTemporaryApi {

    companion object {
        private const val ENDPOINT_TRANSACTION_TEMPORARY = "/api/transaction/temporary"
    }

    @ApiOperation("Get list of transactions")
    @PageableApiParam
    @PostMapping(
        ENDPOINT_TRANSACTION_TEMPORARY,
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun getList(pageable: PageableDTO): PageDTO<TransactionTemporaryDTO>

}
