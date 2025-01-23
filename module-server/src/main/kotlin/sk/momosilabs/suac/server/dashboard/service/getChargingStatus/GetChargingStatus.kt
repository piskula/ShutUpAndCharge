package sk.momosilabs.suac.server.dashboard.service.getChargingStatus

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatus
import sk.momosilabs.suac.server.dashboard.model.charging.ChargerStatusEnum
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.util.Random

@Service
open class GetChargingStatus(
): GetChargingStatusUseCase {

    @Transactional(readOnly = true)
    override fun getChargerStatus(): ChargerStatus {
        val status = ChargerStatusEnum.entries.random()
        val time = getTime()
        val duration = Duration.between(time, Instant.now()).toMinutes()
        return ChargerStatus(
            status = status,
            timestamp = if (status == ChargerStatusEnum.Charging) time else null,
            chargedKwh = if (status == ChargerStatusEnum.Charging)
                BigDecimal.valueOf(183_33, 2).multiply(BigDecimal.valueOf(duration)).divide(BigDecimal.valueOf(1000)).setScale(3, RoundingMode.FLOOR)
            else BigDecimal.ZERO,
        )
    }

    private fun getTime(): Instant {
        return Instant.now()
            .minusSeconds(15 * 60)
            .minusSeconds(Random().nextLong(2 * 60 * 60))
    }

}
