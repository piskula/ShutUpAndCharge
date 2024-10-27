package sk.momosilabs.suac.server.account.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.account.entity.AccountEntity

@Repository
interface AccountRepository: JpaRepository<sk.momosilabs.suac.server.account.entity.AccountEntity, Long> {

    fun findByIdKeycloak(idKeycloak: String): sk.momosilabs.suac.server.account.entity.AccountEntity?

}
