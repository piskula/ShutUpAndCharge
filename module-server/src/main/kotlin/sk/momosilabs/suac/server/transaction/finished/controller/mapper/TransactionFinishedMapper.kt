package sk.momosilabs.suac.server.transaction.finished.controller.mapper

import sk.momosilabs.suac.api.dashboard.dto.MonthlyTransactionSummaryDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFinishedDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFilterDTO
import sk.momosilabs.suac.server.transaction.finished.model.MonthlyTransactionSummary
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinishedFilter
import java.time.ZoneOffset

fun TransactionFinished.toDto() = TransactionFinishedDTO(
    guid = guid,
    time = time.atOffset(ZoneOffset.UTC),
    kwh = kwh,
    price = price,
    chargingStationId = chargingStationId,
    accountId = accountId,
    accountName = accountName,
    triggeredByChipUid = triggeredByChipUid,
    link = link,
)

fun MonthlyTransactionSummary.toDto() = MonthlyTransactionSummaryDTO(
    year = year,
    month = month,
    monthStart = monthStart.atStartOfDay().atOffset(ZoneOffset.UTC),
    negativeCount = negativeCount,
    positiveCount = positiveCount,
    negativeSum = negativeSum,
    positiveSum = positiveSum,
    totalSum = totalSum,
)

fun TransactionFilterDTO.toModel() = TransactionFinishedFilter(
    timeFrom = timeFrom?.toInstant(),
    timeTo = timeTo?.toInstant(),
    kwhFrom = kwhTo,
    kwhTo = kwhTo,
    priceFrom = priceFrom,
    priceTo = priceTo,
    onlyCredit = onlyCredit,
    chargingStationIds = chargingStationIds,
    accountIds = accountIds,
)
