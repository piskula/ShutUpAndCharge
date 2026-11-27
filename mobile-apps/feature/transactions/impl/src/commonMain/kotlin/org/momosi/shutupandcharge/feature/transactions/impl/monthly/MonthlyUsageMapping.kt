package org.momosi.shutupandcharge.feature.transactions.impl.monthly

import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhChart
import kotlin.math.roundToInt
import kotlin.time.Instant

internal data class SeriesShare(val seriesIndex: Int, val kwh: Double, val percent: Int)

internal fun MonthlyKwhChart.monthTotals(): List<Double> = months.indices.map { index -> series.sumOf { it.kwh.getOrElse(index) { 0.0 } } }

internal fun MonthlyKwhChart.sharesOf(monthIndex: Int): List<SeriesShare> {
    val total = series.sumOf { it.kwh.getOrElse(monthIndex) { 0.0 } }
    if (total <= 0.0) return emptyList()
    return series.mapIndexedNotNull { index, s ->
        val kwh = s.kwh.getOrElse(monthIndex) { 0.0 }
        if (kwh <= 0.0) null else SeriesShare(seriesIndex = index, kwh = kwh, percent = (kwh / total * 100).roundToInt())
    }
}

/** Middle of the month in UTC, so formatting it shows the same month in every time zone. */
internal fun monthMidpoint(month: String): Instant? {
    val parts = month.split("-")
    val year = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val monthNumber = parts.getOrNull(1)?.toIntOrNull()?.takeIf { it in 1..12 } ?: return null
    return Instant.parse("${year.toString().padStart(4, '0')}-${monthNumber.toString().padStart(2, '0')}-15T12:00:00Z")
}
