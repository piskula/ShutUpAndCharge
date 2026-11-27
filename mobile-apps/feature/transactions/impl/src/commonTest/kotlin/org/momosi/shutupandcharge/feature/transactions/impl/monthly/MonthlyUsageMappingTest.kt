package org.momosi.shutupandcharge.feature.transactions.impl.monthly

import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhChart
import org.momosi.shutupandcharge.core.transaction.model.MonthlyKwhSeries
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class MonthlyUsageMappingTest {

    private val chart = MonthlyKwhChart(
        months = listOf("2026-10", "2026-11"),
        series = listOf(
            MonthlyKwhSeries(name = "Martin Styk", isOther = false, kwh = listOf(45.0, 70.0)),
            MonthlyKwhSeries(name = "Jana Kováčová", isOther = false, kwh = listOf(30.0, 22.0)),
            MonthlyKwhSeries(name = "Other", isOther = true, kwh = listOf(0.0, 6.0)),
        ),
    )

    @Test
    fun sumsEverySeriesPerMonth() {
        assertEquals(listOf(75.0, 98.0), chart.monthTotals())
    }

    @Test
    fun sharesSkipSeriesThatDidNotChargeThatMonth() {
        assertEquals(
            listOf(SeriesShare(seriesIndex = 0, kwh = 45.0, percent = 60), SeriesShare(seriesIndex = 1, kwh = 30.0, percent = 40)),
            chart.sharesOf(0),
        )
        assertEquals(listOf(71, 22, 6), chart.sharesOf(1).map { it.percent })
    }

    @Test
    fun monthWithoutChargingHasNoShares() {
        val empty = chart.copy(series = chart.series.map { it.copy(kwh = listOf(0.0, 0.0)) })

        assertEquals(emptyList(), empty.sharesOf(1))
    }

    @Test
    fun parsesBackendMonthKeysToMidMonth() {
        assertEquals(Instant.parse("2026-11-15T12:00:00Z"), monthMidpoint("2026-11"))
        assertEquals(Instant.parse("2026-03-15T12:00:00Z"), monthMidpoint("2026-3"))
        assertNull(monthMidpoint("2026-13"))
        assertNull(monthMidpoint("november"))
    }
}
