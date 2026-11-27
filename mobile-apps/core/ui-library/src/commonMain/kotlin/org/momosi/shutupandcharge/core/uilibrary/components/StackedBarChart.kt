package org.momosi.shutupandcharge.core.uilibrary.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.momosi.shutupandcharge.core.uilibrary.resources.Res
import org.momosi.shutupandcharge.core.uilibrary.resources.chart_empty
import org.momosi.shutupandcharge.core.uilibrary.resources.chart_selected_month
import org.momosi.shutupandcharge.core.uilibrary.theme.AppTheme
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

@Immutable
data class ChartSeries(val name: String, val isOther: Boolean, val values: ImmutableList<Double>)

enum class ChartMode { Total, BySeries }

internal data class ChartAxis(val max: Double, val ticks: List<Double>)

private const val BAR_WIDTH_FRACTION = 0.56f
private const val TICK_COUNT = 4
private val NiceStepFactors = listOf(1.0, 2.0, 2.5, 5.0, 10.0)
private val AxisWidth = 32.dp
private val TopLabelSpace = 22.dp
private val ChartHeight = 208.dp

/**
 * Monthly bars with a labelled value axis. In [ChartMode.BySeries] each bar is stacked by series; in
 * [ChartMode.Total] it shows a single total. Every month is selectable by touch, keyboard and TalkBack.
 */
@Composable
fun StackedBarChart(
    labels: ImmutableList<String>,
    descriptions: ImmutableList<String>,
    totalLabels: ImmutableList<String>,
    series: ImmutableList<ChartSeries>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    mode: ChartMode = ChartMode.BySeries,
) {
    val totals = remember(labels, series) { labels.indices.map { index -> series.sumOf { it.values.getOrElse(index) { 0.0 } } } }
    val maxTotal = totals.maxOrNull() ?: 0.0
    if (maxTotal <= 0.0) {
        Text(
            text = stringResource(Res.string.chart_empty),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant,
            modifier = modifier,
        )
        return
    }
    val axis = remember(maxTotal) { niceAxis(maxTotal) }
    val colors = chartSeriesColors(series)
    val totalColor = AppTheme.colors.chartSeries.first()
    val gridColor = AppTheme.colors.outlineVariant
    val outlineColor = AppTheme.colors.onSurface
    val labelStyle = AppTheme.typography.labelSmall.copy(color = AppTheme.colors.onSurfaceVariant)
    val selectedLabelStyle = labelStyle.copy(color = AppTheme.colors.onSurface, fontWeight = FontWeight.Bold)
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().height(ChartHeight)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val axisWidth = AxisWidth.toPx()
                val top = TopLabelSpace.toPx()
                val plotHeight = size.height - top
                val slotWidth = (size.width - axisWidth) / labels.size
                val barWidth = slotWidth * BAR_WIDTH_FRACTION
                val gap = 3.dp.toPx()
                fun y(value: Double): Float = top + plotHeight * (1 - value / axis.max).toFloat()

                axis.ticks.forEach { tick ->
                    val tickY = y(tick)
                    drawLine(gridColor, Offset(axisWidth, tickY), Offset(size.width, tickY), strokeWidth = 1.dp.toPx())
                    val layout = textMeasurer.measure(tick.toLong().toString(), labelStyle)
                    drawText(layout, topLeft = Offset(axisWidth - 6.dp.toPx() - layout.size.width, tickY - layout.size.height / 2f))
                }

                totals.forEachIndexed { index, total ->
                    val left = axisWidth + slotWidth * index + (slotWidth - barWidth) / 2
                    if (mode == ChartMode.Total) {
                        drawRect(totalColor, Offset(left, y(total)), Size(barWidth, y(0.0) - y(total)))
                    } else {
                        var stacked = 0.0
                        series.forEachIndexed { seriesIndex, s ->
                            val value = s.values.getOrElse(index) { 0.0 }
                            if (value <= 0.0) return@forEachIndexed
                            val segmentTop = y(stacked + value)
                            drawRect(colors[seriesIndex], Offset(left, segmentTop), Size(barWidth, y(stacked) - segmentTop))
                            stacked += value
                        }
                    }
                    val isSelected = index == selectedIndex
                    if (isSelected && total > 0.0) {
                        drawRoundRect(
                            color = outlineColor,
                            topLeft = Offset(left - gap, y(total) - gap),
                            size = Size(barWidth + gap * 2, y(0.0) - y(total) + gap),
                            cornerRadius = CornerRadius(4.dp.toPx()),
                            style = Stroke(width = 1.5.dp.toPx()),
                        )
                    }
                    val valueLayout = textMeasurer.measure(totalLabels.getOrElse(index) { "" }, if (isSelected) selectedLabelStyle else labelStyle)
                    drawText(
                        valueLayout,
                        topLeft = Offset(
                            x = left + barWidth / 2 - valueLayout.size.width / 2f,
                            y = max(0f, y(total) - gap * 2 - valueLayout.size.height),
                        ),
                    )
                }
            }
            Row(modifier = Modifier.fillMaxSize().padding(start = AxisWidth)) {
                labels.indices.forEach { index ->
                    val description = stringResource(
                        Res.string.chart_selected_month,
                        descriptions.getOrElse(index) { labels[index] },
                        totalLabels.getOrElse(index) { "" },
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .selectable(selected = index == selectedIndex, role = Role.Tab, onClick = { onSelect(index) })
                            .semantics { contentDescription = description }
                            .testTag("chart_month_$index"),
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(start = AxisWidth, top = 6.dp)) {
            labels.forEachIndexed { index, label ->
                Text(
                    text = label,
                    style = if (index == selectedIndex) selectedLabelStyle else labelStyle,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun chartSeriesColors(series: List<ChartSeries>): List<Color> {
    val palette = AppTheme.colors.chartSeries
    val other = AppTheme.colors.chartOther
    var paletteIndex = 0
    return series.map { if (it.isOther) other else palette[paletteIndex++ % palette.size] }
}

internal fun niceAxis(maxValue: Double): ChartAxis {
    val rawStep = max(maxValue / TICK_COUNT, 1.0)
    val magnitude = 10.0.pow(floor(log10(rawStep)))
    val step = NiceStepFactors.first { it * magnitude >= rawStep } * magnitude
    val axisMax = ceil(maxValue / step) * step
    val ticks = generateSequence(0.0) { it + step }.takeWhile { it <= axisMax + step / 2 }.toList()
    return ChartAxis(max = axisMax, ticks = ticks)
}

private val PreviewSeries = persistentListOf(
    ChartSeries("Martin", false, persistentListOf(40.0, 52.0, 31.0, 60.0, 45.0, 70.0)),
    ChartSeries("Jana", false, persistentListOf(20.0, 12.0, 25.0, 18.0, 30.0, 22.0)),
    ChartSeries("Other", true, persistentListOf(5.0, 8.0, 0.0, 10.0, 4.0, 6.0)),
)

@Preview(showBackground = true)
@Composable
private fun StackedBarChartPreview() {
    SuacTheme(isDarkTheme = true) {
        StackedBarChart(
            labels = persistentListOf("Jun", "Jul", "Aug", "Sep", "Oct", "Nov"),
            descriptions = persistentListOf("June 2026", "July 2026", "August 2026", "September 2026", "October 2026", "November 2026"),
            totalLabels = persistentListOf("65", "72", "56", "88", "79", "98"),
            series = PreviewSeries,
            selectedIndex = 5,
            onSelect = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StackedBarChartTotalPreview() {
    SuacTheme {
        StackedBarChart(
            labels = persistentListOf("Jun", "Jul", "Aug", "Sep", "Oct", "Nov"),
            descriptions = persistentListOf("June 2026", "July 2026", "August 2026", "September 2026", "October 2026", "November 2026"),
            totalLabels = persistentListOf("65", "72", "56", "88", "79", "98"),
            series = PreviewSeries,
            selectedIndex = 3,
            onSelect = {},
            mode = ChartMode.Total,
        )
    }
}
