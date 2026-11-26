package org.momosi.shutupandcharge.core.transaction.model

import kotlin.time.Instant

data class Transaction(val id: String, val time: Instant?, val kwh: Double, val price: Double, val stationId: String?, val accountName: String, val triggeredByChip: Boolean, val link: String?) {
    val isTopUp: Boolean get() = price > 0
}

data class MonthlySummary(val monthStart: Instant?, val chargingCount: Long, val chargingSum: Double, val topUpCount: Long, val topUpSum: Double, val total: Double)

data class MonthlyKwhChart(val months: List<String>, val series: List<MonthlyKwhSeries>) {
    val isEmpty: Boolean get() = months.isEmpty() || series.all { s -> s.kwh.all { it == 0.0 } }
}

data class MonthlyKwhSeries(val name: String, val isOther: Boolean, val kwh: List<Double>)

data class PendingTransaction(val id: Long, val number: Int, val identifier: String, val energyMeter: Long, val startedAt: Instant?, val accountName: String)

enum class TransactionType {
    All,
    TopUps,
    Charging,
}

data class TransactionFilter(val onlyAccountKeycloakId: String? = null, val type: TransactionType = TransactionType.All)

data class ExportedFile(val bytes: ByteArray, val fileName: String, val mimeType: String) {
    override fun equals(other: Any?): Boolean = other is ExportedFile && fileName == other.fileName && bytes.contentEquals(other.bytes)

    override fun hashCode(): Int = 31 * fileName.hashCode() + bytes.contentHashCode()
}
