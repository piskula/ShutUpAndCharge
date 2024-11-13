package sk.momosilabs.suac.server.account.service.setUserVerifiedFlag

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.account.persistence.AccountPersistence
import sk.momosilabs.suac.server.common.IsAdmin

@Service
open class SetUserVerifiedFlag(
    private val accountPersistence: AccountPersistence,
): SetUserVerifiedFlagUseCase {

    @IsAdmin
    @Transactional
    override fun setFlag(accountId: Long, isVerified: Boolean): Boolean =
        accountPersistence.setVerifiedForCharging(accountId, isVerified)

}
