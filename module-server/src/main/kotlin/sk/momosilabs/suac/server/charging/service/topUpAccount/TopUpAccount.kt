package sk.momosilabs.suac.server.charging.service.topUpAccount

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import sk.momosilabs.suac.server.charging.persistence.ChargingPersistence
import sk.momosilabs.suac.server.common.IsAdmin
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.UUID

@Service
open class TopUpAccount(
    private val chargingPersistence: ChargingPersistence,
): TopUpAccountUseCase {

    @IsAdmin
    @Transactional
    override fun topUp(accountId: Long, amount: BigDecimal): BigDecimal {
        val creditToSave = ChargingListItem(
            id = 0L,
            guid = UUID.randomUUID(),
            time = Instant.now(),
            kwh = BigDecimal.ZERO,
            price = amount.setScale(2, RoundingMode.FLOOR).abs(),
            chargingStationId = "",
        )
        return chargingPersistence.saveFinishedCharging(creditToSave, accountId).price
    }

}
