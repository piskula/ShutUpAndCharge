package sk.momosilabs.suac.api.dashboard.dto

import java.math.BigDecimal
import java.time.OffsetDateTime

data class MonthlyTransactionSummaryDTO(
    val year: Int,
    val month: Int,
    val monthStart: OffsetDateTime,
    val negativeCount: Long,
    val positiveCount: Long,
    val negativeSum: BigDecimal,
    val positiveSum: BigDecimal,
    val totalSum: BigDecimal,
)
