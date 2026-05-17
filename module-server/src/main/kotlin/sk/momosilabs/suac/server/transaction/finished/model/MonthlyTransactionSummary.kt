package sk.momosilabs.suac.server.transaction.finished.model

import java.math.BigDecimal
import java.time.LocalDate

data class MonthlyTransactionSummary(
    val year: Int,
    val month: Int,
    val monthStart: LocalDate,
    val negativeCount: Long,
    val positiveCount: Long,
    val negativeSum: BigDecimal,
    val positiveSum: BigDecimal,
    val totalSum: BigDecimal,
)
