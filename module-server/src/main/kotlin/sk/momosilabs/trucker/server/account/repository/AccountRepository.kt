package sk.momosilabs.trucker.server.account.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.trucker.server.account.entity.AccountEntity

@Repository
interface AccountRepository: JpaRepository<AccountEntity, String>
