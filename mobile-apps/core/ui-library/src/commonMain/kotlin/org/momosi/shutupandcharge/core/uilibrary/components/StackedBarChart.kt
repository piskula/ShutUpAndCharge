package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.momosi.shutupandcharge.core.uilibrary.resources.Res
import org.momosi.shutupandcharge.core.uilibrary.resources.chart_empty
import org.momosi.shutupandcharge.core.uilibrary.resources.chart_other
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Immutable
data class ChartSeries(val name: String, val isOther: Boolean, val values: ImmutableList<Double>)

private val SeriesPalette = listOf(
    Color(0xFF005CBB),
    Color(0xFFE8710A),
    Color(0xFF1E8E3E),
    Color(0xFFD93025),
    Color(0xFF9334E6),
)
private val OtherColor = Color(0xFF9E9E9E)
private const val BAR_WIDTH_FRACTION = 0.6f

@Composable
fun StackedBarChart(
    labels: ImmutableList<String>,
    series: ImmutableList<ChartSeries>,
    maxValueText: String,
    modifier: Modifier = Modifier,
) {
    val totals = labels.indices.map { index -> series.sumOf { it.values.getOrElse(index) { 0.0 } } }
    val maxTotal = totals.maxOrNull() ?: 0.0
    val colors = seriesColors(series)
    val gridColor = AppTheme.colors.outlineVariant

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (maxTotal <= 0.0) {
            Text(
                text = stringResource(Res.string.chart_empty),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant,
            )
            return@Column
        }
        Text(text = maxValueText, style = AppTheme.typography.labelSmall, color = AppTheme.colors.onSurfaceVariant)
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            drawLine(gridColor, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 1.dp.toPx())
            drawLine(gridColor, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
            val slotWidth = size.width / labels.size
            val barWidth = slotWidth * BAR_WIDTH_FRACTION
            labels.indices.forEach { index ->
                var top = size.height
                series.forEachIndexed { seriesIndex, s ->
                    val value = s.values.getOrElse(index) { 0.0 }
                    if (value <= 0.0) return@forEachIndexed
                    val barHeight = (value / maxTotal * size.height).toFloat()
                    top -= barHeight
                    drawRoundRect(
                        color = colors[seriesIndex],
                        topLeft = Offset(slotWidth * index + (slotWidth - barWidth) / 2, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(2.dp.toPx()),
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Legend(series = series, colors = colors)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Legend(series: ImmutableList<ChartSeries>, colors: List<Color>) {
    val otherName = stringResource(Res.string.chart_other)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        series.forEachIndexed { index, s ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(10.dp).background(colors[index], CircleShape))
                Text(
                    text = if (s.isOther) otherName else s.name,
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}

private fun seriesColors(series: List<ChartSeries>): List<Color> {
    var paletteIndex = 0
    return series.map { if (it.isOther) OtherColor else SeriesPalette[paletteIndex++ % SeriesPalette.size] }
}

@Preview(showBackground = true)
@Composable
private fun StackedBarChartPreview() {
    SuacTheme {
        StackedBarChart(
            labels = persistentListOf("06", "07", "08", "09", "10", "11"),
            series = persistentListOf(
                ChartSeries("Martin", false, persistentListOf(40.0, 52.0, 31.0, 60.0, 45.0, 70.0)),
                ChartSeries("Jana", false, persistentListOf(20.0, 12.0, 25.0, 18.0, 30.0, 22.0)),
                ChartSeries("Other", true, persistentListOf(5.0, 8.0, 0.0, 10.0, 4.0, 6.0)),
            ),
            maxValueText = "98 kWh",
        )
    }
}
