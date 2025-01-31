package sk.momosilabs.suac.server.account.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.account.dto.AccountDTO
import sk.momosilabs.suac.api.account.AccountApi
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.server.account.controller.mapper.toDto
import sk.momosilabs.suac.server.account.model.Account
import sk.momosilabs.suac.server.account.service.assignChipUid.AssignChipUidUseCase
import sk.momosilabs.suac.server.account.service.getUserList.GetUserListUseCase
import sk.momosilabs.suac.server.account.service.setUserVerifiedFlag.SetUserVerifiedFlagUseCase
import sk.momosilabs.suac.server.common.toDto
import sk.momosilabs.suac.server.common.toModel
import sk.momosilabs.suac.server.transaction.finished.service.topUpAccount.TopUpAccountUseCase
import java.math.BigDecimal

@RestController
class AccountController(
    private val getUserList: GetUserListUseCase,
    private val setUserVerifiedFlag: SetUserVerifiedFlagUseCase,
    private val assignChipUid: AssignChipUidUseCase,
    private val topUpAccount: TopUpAccountUseCase,
) : AccountApi {

    override fun getUserList(pageable: PageableDTO): PageDTO<AccountDTO> =
        getUserList.get(pageable.toModel()).toDto(Account::toDto)

    override fun updateVerifiedFlag(accountId: Long, verified: Boolean): Boolean =
        setUserVerifiedFlag.setFlag(accountId, verified)

    override fun updateAssignedChipUid(accountId: Long, chipUid: String?) =
        assignChipUid.assignChipUid(accountId, chipUid)

    override fun topUpAccount(accountId: Long, amount: BigDecimal): BigDecimal =
        topUpAccount.topUp(accountId, amount)

}
