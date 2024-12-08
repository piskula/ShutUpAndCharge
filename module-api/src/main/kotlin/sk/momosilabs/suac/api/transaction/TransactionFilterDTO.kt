package sk.momosilabs.suac.api.transaction

import java.math.BigDecimal
import java.time.OffsetDateTime

data class TransactionFilterDTO(
    val timeFrom: OffsetDateTime? = null,
    val timeTo: OffsetDateTime? = null,
    val kwhFrom: BigDecimal? = null,
    val kwhTo: BigDecimal? = null,
    val priceFrom: BigDecimal? = null,
    val priceTo: BigDecimal? = null,
    val onlyCredit: Boolean? = null,
    val chargingStationIds: Set<String> = emptySet(),
    val accountIds: Set<Long> = emptySet(),
)
