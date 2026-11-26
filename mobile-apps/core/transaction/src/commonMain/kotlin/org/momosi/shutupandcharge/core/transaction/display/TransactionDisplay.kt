package org.momosi.shutupandcharge.core.transaction.display

import org.momosi.shutupandcharge.core.common.formatter.DateFormatter
import org.momosi.shutupandcharge.core.common.formatter.NumberFormatter
import org.momosi.shutupandcharge.core.transaction.model.Transaction

data class TransactionDisplay(
    val id: String,
    val dateText: String,
    val stationId: String?,
    val accountName: String,
    val priceText: String,
    val price: Double,
    val kwhText: String?,
    val isTopUp: Boolean,
    val triggeredByChip: Boolean,
    val link: String?,
)

fun Transaction.toDisplay(dateFormatter: DateFormatter, numberFormatter: NumberFormatter): TransactionDisplay = TransactionDisplay(
    id = id,
    dateText = time?.let(dateFormatter::formatFullDateTime).orEmpty(),
    stationId = stationId,
    accountName = accountName,
    priceText = numberFormatter.formatEur(price),
    price = price,
    kwhText = if (isTopUp) null else numberFormatter.formatKwh(kwh),
    isTopUp = isTopUp,
    triggeredByChip = triggeredByChip,
    link = link,
)
