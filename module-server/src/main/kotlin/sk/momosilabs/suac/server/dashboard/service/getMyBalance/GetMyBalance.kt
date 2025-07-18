package sk.momosilabs.suac.server.dashboard.service.getMyBalance

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence
import sk.momosilabs.suac.server.common.IsUser
import sk.momosilabs.suac.server.security.service.CurrentUserService
import java.math.BigDecimal

@Service
open class GetMyBalance(
    private val currentUserService: CurrentUserService,
    private val transactionPersistence: TransactionFinishedPersistence,
): GetMyBalanceUseCase {

    @IsUser
    @Transactional(readOnly = true)
    override fun getCurrentBalance(): BigDecimal {
        val userId = currentUserService.userId()
        return transactionPersistence.sumUpForUsers(setOf(userId)).getOrDefault(userId, BigDecimal.ZERO)
    }

}
