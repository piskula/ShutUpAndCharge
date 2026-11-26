package org.momosi.shutupandcharge.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionFinishedDto(
    val guid: String,
    val time: String,
    val kwh: Double,
    val price: Double,
    val chargingStationId: String? = null,
    val accountId: Long,
    val accountName: String,
    val triggeredByChipUid: Boolean = false,
    val link: String? = null,
)

@Serializable
data class TransactionFilterDto(
    val timeFrom: String? = null,
    val timeTo: String? = null,
    val kwhFrom: Double? = null,
    val kwhTo: Double? = null,
    val priceFrom: Double? = null,
    val priceTo: Double? = null,
    val onlyCredit: Boolean? = null,
    val chargingStationIds: Set<String> = emptySet(),
    val accountIds: Set<String> = emptySet(),
)

@Serializable
data class MonthlyTransactionSummaryDto(val year: Int, val month: Int, val monthStart: String, val negativeCount: Long, val positiveCount: Long, val negativeSum: Double, val positiveSum: Double, val totalSum: Double)

@Serializable
data class MonthlyKwhChartDto(val months: List<String>, val series: List<MonthlyKwhSeriesDto>)

@Serializable
data class MonthlyKwhSeriesDto(val accountId: Long? = null, val name: String, val other: Boolean, val kwh: List<Double>)

@Serializable
data class TransactionTemporaryDto(val id: Long, val trxNumber: Int, val trxIdentifier: String, val energyMeter: Long, val timeStart: String, val accountId: Long, val accountName: String)
