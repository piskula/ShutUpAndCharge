package sk.momosilabs.suac.server.transaction.finished.model

import java.time.YearMonth

data class MonthlyKwhChart(
    val months: List<YearMonth>,
    val series: List<MonthlyKwhSeries>,
)
