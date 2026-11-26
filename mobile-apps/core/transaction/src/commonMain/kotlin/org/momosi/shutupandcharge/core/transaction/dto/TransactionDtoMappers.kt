package org.momosi.shutupandcharge.core.transaction.dto

import org.momosi.shutupandcharge.core.network.dto.toInstantOrNull
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhChart
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhSeries
import org.momosi.shutupandcharge.core.transaction.model.MonthlySummary
import org.momosi.shutupandcharge.core.transaction.model.PendingTransaction
import org.momosi.shutupandcharge.core.transaction.model.Transaction
import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter
import org.momosi.shutupandcharge.core.transaction.model.TransactionType
import org.momosi.shutupandcharge.dto.MonthlyKwhChartDto
import org.momosi.shutupandcharge.dto.MonthlyTransactionSummaryDto
import org.momosi.shutupandcharge.dto.TransactionFilterDto
import org.momosi.shutupandcharge.dto.TransactionFinishedDto
import org.momosi.shutupandcharge.dto.TransactionTemporaryDto

internal fun TransactionFinishedDto.toTransaction(): Transaction = Transaction(
    id = guid,
    time = time.toInstantOrNull(),
    kwh = kwh,
    price = price,
    stationId = chargingStationId,
    accountName = accountName,
    triggeredByChip = triggeredByChipUid,
    link = link?.takeIf { it.isNotBlank() },
)

internal fun MonthlyTransactionSummaryDto.toMonthlySummary(): MonthlySummary = MonthlySummary(
    monthStart = monthStart.toInstantOrNull(),
    chargingCount = negativeCount,
    chargingSum = negativeSum,
    topUpCount = positiveCount,
    topUpSum = positiveSum,
    total = totalSum,
)

internal fun MonthlyKwhChartDto.toMonthlyKwhChart(): MonthlyKwhChart = MonthlyKwhChart(
    months = months,
    series = series.map { MonthlyKwhSeries(name = it.name, isOther = it.other, kwh = it.kwh) },
)

internal fun TransactionTemporaryDto.toPendingTransaction(): PendingTransaction = PendingTransaction(
    id = id,
    number = trxNumber,
    identifier = trxIdentifier,
    energyMeter = energyMeter,
    startedAt = timeStart.toInstantOrNull(),
    accountName = accountName,
)

internal fun TransactionFilter.toDto(): TransactionFilterDto = TransactionFilterDto(
    onlyCredit = when (type) {
        TransactionType.All -> null
        TransactionType.TopUps -> true
        TransactionType.Charging -> false
    },
    accountIds = onlyAccountKeycloakId?.let { setOf(it) }.orEmpty(),
)
