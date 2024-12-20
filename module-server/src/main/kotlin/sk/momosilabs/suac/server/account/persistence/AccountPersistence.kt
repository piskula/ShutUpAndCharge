package sk.momosilabs.suac.server.account.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sk.momosilabs.suac.server.account.model.Account

interface AccountPersistence {

    fun findAll(pageable: Pageable): Page<Account>

    fun setVerifiedForCharging(accountId: Long, verified: Boolean): Boolean

}
