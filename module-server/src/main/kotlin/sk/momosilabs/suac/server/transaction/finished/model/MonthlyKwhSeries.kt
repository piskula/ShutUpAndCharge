package sk.momosilabs.suac.server.transaction.finished.model

import java.math.BigDecimal

data class MonthlyKwhSeries(
    val accountId: Long?,
    val name: String,
    val other: Boolean,
    val kwh: List<BigDecimal>,
)
