package sk.momosilabs.suac.api.transaction.finished.dto

import java.math.BigDecimal

data class MonthlyKwhSeriesDTO(
    val accountId: Long?,
    val name: String,
    val other: Boolean,
    val kwh: List<BigDecimal>,
)
