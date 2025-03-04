package sk.momosilabs.suac.api.transaction.finished.dto

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class TransactionFinishedDTO(
    val guid: UUID,
    val time: OffsetDateTime,
    val kwh: BigDecimal,
    val price: BigDecimal,
    val chargingStationId: String?,
    val accountId: Long,
    val accountName: String,
    val triggeredByChipUid: Boolean,
    val link: String?,
)
