package sk.momosilabs.suac.server.transaction.finished.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class ChargingToCreate(
    val guid: UUID,
    val userId: Long,
    val timeStart: Instant,
    val timeEnd: Instant,
    val kwh: BigDecimal,
    val stationId: String?,
    val price: BigDecimal,
    val stationSession: Long?,
    val energyMeter: Long?,
    val chipUid: String?,
    val link: String?,
)
