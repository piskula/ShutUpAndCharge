package sk.momosilabs.suac.server.account.service.getUserList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.account.model.Account

interface GetUserListUseCase {

    fun get(pageable: Pageable): Page<Account>

}
