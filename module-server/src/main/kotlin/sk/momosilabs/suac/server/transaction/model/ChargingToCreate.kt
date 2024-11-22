package sk.momosilabs.suac.server.transaction.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class ChargingToCreate(
    val guid: UUID,
    val time: Instant,
    val kwh: BigDecimal,
    val price: BigDecimal,
    val chargingStationId: String,
)
