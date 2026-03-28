package sk.momosilabs.suac.api.transaction.temporary

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.temporary.dto.TransactionTemporaryDTO

@Tag(name = "Temporary Transaction")
interface TransactionTemporaryApi {

    companion object {
        private const val ENDPOINT_TRANSACTION_TEMPORARY = "/api/transaction/temporary"
    }

    @Operation(summary = "Get list of transactions")
    @PostMapping(
        ENDPOINT_TRANSACTION_TEMPORARY,
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun getList(pageable: PageableDTO): PageDTO<TransactionTemporaryDTO>

    @Operation(summary = "Eforce download and sync")
    @PostMapping("$ENDPOINT_TRANSACTION_TEMPORARY/sync")
    fun enforceDownloadAndSync()

}
