package sk.momosilabs.suac.api.dashboard.dto

import java.math.BigDecimal
import java.time.OffsetDateTime

data class ChargerStatusDTO(
    val ready: Boolean,
    val offline: Boolean,
    val occupied: Boolean,
    val occupiedFrom: OffsetDateTime? = null,
    val chargedKwh: BigDecimal,
    val awaitingAuthorization: Boolean,
)
