package sk.momosilabs.suac.server.account.service.getUserList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.model.Account
import sk.momosilabs.suac.server.account.persistence.AccountPersistence
import sk.momosilabs.suac.server.common.IsAdmin

@Service
open class GetUserList(
    private val accountPersistence: AccountPersistence,
): GetUserListUseCase {

    @IsAdmin
    @Transactional(readOnly = true)
    override fun get(pageable: Pageable): Page<Account> =
        accountPersistence.findAll(pageable)

}
