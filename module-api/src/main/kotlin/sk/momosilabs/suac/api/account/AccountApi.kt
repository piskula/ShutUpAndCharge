package sk.momosilabs.suac.api.charging

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.account.dto.AccountDTO
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO

@Api("Account")
interface AccountApi {

    companion object {
        const val ENDPOINT_CHARGING = "/api/account"
    }

    @ApiOperation("List users in system")
    // TODO check if can be extracted to custom annotation
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer", dataTypeClass = Integer::class),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer", dataTypeClass = Integer::class),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", dataTypeClass = String::class)
    )
    @GetMapping(ENDPOINT_CHARGING, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserList(pageable: PageableDTO): PageDTO<AccountDTO>

}
