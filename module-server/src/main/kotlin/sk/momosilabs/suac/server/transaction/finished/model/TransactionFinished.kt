package sk.momosilabs.suac.server.transaction.finished.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TransactionFinished(
    val id: Long,
    val guid: UUID,
    val time: Instant,
    val kwh: BigDecimal,
    val price: BigDecimal,
    val chargingStationId: String?,
    val accountId: Long,
    val accountName: String,
    val triggeredByChipUid: Boolean,
    val link: String?,
)
