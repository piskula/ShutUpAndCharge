package sk.momosilabs.suac.server.transaction.finished.model

import java.math.BigDecimal
import java.time.Instant

data class MonthlyTransactionSummary(
    val year: Int,
    val month: Int,
    val monthStart: Instant,
    val negativeCount: Long,
    val positiveCount: Long,
    val negativeSum: BigDecimal,
    val positiveSum: BigDecimal,
    val totalSum: BigDecimal,
)
