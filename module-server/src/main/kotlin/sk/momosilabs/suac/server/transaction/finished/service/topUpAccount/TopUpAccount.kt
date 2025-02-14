package sk.momosilabs.suac.server.transaction.finished.service.topUpAccount

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.transaction.finished.persistence.TransactionFinishedPersistence
import sk.momosilabs.suac.server.common.IsAdmin
import sk.momosilabs.suac.server.transaction.finished.model.ChargingToCreate
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.UUID

@Service
open class TopUpAccount(
    private val chargingPersistence: TransactionFinishedPersistence,
): TopUpAccountUseCase {

    @IsAdmin
    @Transactional
    override fun topUp(accountId: Long, amount: BigDecimal): BigDecimal {
        val now = Instant.now()
        val creditToSave = ChargingToCreate(
            guid = UUID.randomUUID(),
            userId = accountId,
            timeStart = now,
            timeEnd = now,
            kwh = BigDecimal.ZERO,
            stationId = null,
            price = amount.setScale(2, RoundingMode.FLOOR).abs(),
            stationSession = null,
            energyMeter = null,
            chipUid = null,
            link = null,
        )
        return chargingPersistence.saveFinishedCharging(creditToSave).price
    }

}
