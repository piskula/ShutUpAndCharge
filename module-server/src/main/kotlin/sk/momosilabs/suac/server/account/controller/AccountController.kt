package sk.momosilabs.suac.server.account.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.account.dto.AccountDTO
import sk.momosilabs.suac.api.charging.AccountApi
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.server.account.controller.mapper.toDto
import sk.momosilabs.suac.server.account.model.Account
import sk.momosilabs.suac.server.account.service.getUserList.GetUserListUseCase
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel

@RestController
class AccountController(
    private val getUserList: GetUserListUseCase,
) : AccountApi {

    override fun getUserList(pageable: PageableDTO): PageDTO<AccountDTO> =
        getUserList.get(pageable.toModel()).toDto(Account::toDto)

}
