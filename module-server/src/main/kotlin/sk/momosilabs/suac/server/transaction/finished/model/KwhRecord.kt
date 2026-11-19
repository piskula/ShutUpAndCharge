package sk.momosilabs.suac.server.transaction.finished.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class KwhRecord(
    val accountId: Long,
    val accountName: String,
    val timeStartUtc: LocalDateTime,
    val kwh: BigDecimal,
)
