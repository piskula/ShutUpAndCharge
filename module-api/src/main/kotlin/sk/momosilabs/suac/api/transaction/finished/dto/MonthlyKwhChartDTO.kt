package sk.momosilabs.suac.api.transaction.finished.dto

data class MonthlyKwhChartDTO(
    val months: List<String>,
    val series: List<MonthlyKwhSeriesDTO>,
)
