package sk.momosilabs.suac.server.transaction.model

import java.math.BigDecimal
import java.time.Instant

data class TransactionFilter(
    val timeFrom: Instant? = null,
    val timeTo: Instant? = null,
    val kwhFrom: BigDecimal? = null,
    val kwhTo: BigDecimal? = null,
    val priceFrom: BigDecimal? = null,
    val priceTo: BigDecimal? = null,
    val onlyCredit: Boolean? = null,
    val chargingStationIds: Set<String> = emptySet(),
    val accountIds: Set<Long> = emptySet(),
)
