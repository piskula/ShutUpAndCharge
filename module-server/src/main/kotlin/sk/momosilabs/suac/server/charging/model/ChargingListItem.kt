package sk.momosilabs.suac.server.charging.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class ChargingListItem(
    val id: Long,
    val guid: UUID,
    val time: Instant,
    val kwh: BigDecimal,
    val chargingStationId: String,
)
