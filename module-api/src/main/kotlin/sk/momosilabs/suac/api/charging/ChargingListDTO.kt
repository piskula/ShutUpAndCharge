package sk.momosilabs.suac.api.charging

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class ChargingListDTO(
    val guid: UUID,
    val time: OffsetDateTime,
    val kwh: BigDecimal,
    val chargingStationId: String,
)
