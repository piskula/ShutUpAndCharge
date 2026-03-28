package sk.momosilabs.suac.api.account

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import sk.momosilabs.suac.api.account.dto.AccountDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import java.math.BigDecimal

@Tag(name = "Account")
interface AccountApi {

    companion object {
        private const val ENDPOINT_ACCOUNT = "/api/account"
    }

    @Operation(summary = "List users in system")
    @GetMapping(ENDPOINT_ACCOUNT, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserList(pageable: PageableDTO): PageDTO<AccountDTO>

    @Operation(summary = "Set or unset verified flag for user")
    @PostMapping("$ENDPOINT_ACCOUNT/{accountId}/verified/{verified}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateVerifiedFlag(@PathVariable accountId: Long, @PathVariable verified: Boolean): Boolean

    @Operation(summary = "Set or unset charging chip for user")
    @PostMapping("$ENDPOINT_ACCOUNT/{accountId}/chipUid", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAssignedChipUid(@PathVariable accountId: Long, @RequestBody chipUid: String?)

    @Operation(summary = "Top up credit to user account")
    @PostMapping(
        "${ENDPOINT_ACCOUNT}/{accountId}/topUp",
        produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun topUpAccount(@PathVariable accountId: Long, @RequestBody amount: BigDecimal): BigDecimal

}
