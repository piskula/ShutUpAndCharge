package sk.momosilabs.suac.api.account

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import sk.momosilabs.suac.api.account.dto.AccountDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableApiParam
import sk.momosilabs.suac.api.common.dto.PageableDTO

@Api("Account")
interface AccountApi {

    companion object {
        private const val ENDPOINT_ACCOUNT = "/api/account"
    }

    @ApiOperation("List users in system")
    @PageableApiParam
    @GetMapping(ENDPOINT_ACCOUNT, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserList(pageable: PageableDTO): PageDTO<AccountDTO>

    @PostMapping("$ENDPOINT_ACCOUNT/{accountId}/verified/{verified}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateVerifiedFlag(@PathVariable accountId: Long, @PathVariable verified: Boolean): Boolean

}
