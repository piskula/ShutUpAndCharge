package sk.momosilabs.suac.server.dashboard.model.charging

import java.math.BigDecimal
import java.time.Instant

data class ChargerStatus(
    val status: ChargerStatusEnum,
    val timestamp: Instant?,
    val chargedKwh: BigDecimal,
)
