package sk.momosilabs.suac.server.charging.temporary

import org.springframework.stereotype.Service
import sk.momosilabs.suac.server.charging.model.ChargingListItem
import sk.momosilabs.suac.server.charging.persistence.ChargingPersistence
import sk.momosilabs.suac.server.charging.persistence.repository.ChargingFinishedRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Random
import java.util.UUID

@Service
open class FakeChargingTemporaryService(
    private val chargingRepository: ChargingFinishedRepository,
    private val chargingPersistence: ChargingPersistence,
) {

    fun mockChargingForUser(userId: Long) {
        val count = chargingRepository.countByAccountId(userId)
        if (count < 10) {
            val kwh = BigDecimal.valueOf(Random().nextLong(50000) + 10000L, 3)
            val price = kwh.multiply(BigDecimal.valueOf(29L, 2))
                .setScale(2, RoundingMode.HALF_UP)
                .negate()
            chargingPersistence.saveFinishedCharging(
                charging = ChargingListItem(
                    id = 0L,
                    guid = UUID.randomUUID(),
                    time = LocalDateTime.now()
                        .minusMonths(Random().nextLong(16))
                        .minusDays(Random().nextLong(30))
                        .minusHours(Random().nextLong(24))
                        .toInstant(ZoneOffset.UTC),
                    kwh = kwh,
                    price = price,
                    chargingStationId = "ETCC:Kutlik:${Random().nextInt(2)+1}",
                ),
                userId = userId,
            )
        }
    }

}
