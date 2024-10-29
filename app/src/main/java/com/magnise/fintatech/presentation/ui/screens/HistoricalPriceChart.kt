package com.magnise.fintatech.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magnise.fintatech.data.models.HistoricalPriceData
import com.magnise.fintatech.presentation.ui.theme.MagniseTheme

/**
 * A composable function to render a line chart representing historical price data.
 *
 * @param data List of historical price data to plot on the chart.
 */
@Composable
fun HistoricalPriceChart(
    data: List<HistoricalPriceData>
) {
    if (data.isEmpty()) return
    val chartColor = MaterialTheme.colorScheme.primary

    // Calculating maximum price with a 10% buffer for Y-axis scaling
    val maxPrice = data.maxOf { it.closePrice } * 1.1
    val minPrice = 0.0

    Column{
        Text(
            text = "Charting data:",
            style = MaterialTheme.typography.titleSmall
        )

        OutlinedCard(
            modifier = Modifier
                .size(width = 300.dp, height = 220.dp)  // Adjusted height for bottom label space
                .padding(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 24.dp)
                ) {
                    val path = Path()
                    val widthPerDataPoint = size.width / (data.size - 1)
                    val heightRange = maxPrice - minPrice

                    // Calculate step size for Y-axis labels (10% of max price, rounded to nearest unit)
                    val yStep = (maxPrice / 10).toInt()

                    // Draw vertical and horizontal axes
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Draw Y-axis labels and ticks
                    for (i in 1..10) {
                        val yLabel = yStep * i
                        val yPosition = size.height - (yLabel / maxPrice * size.height).toFloat()
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                "$yLabel",
                                10.dp.toPx(),
                                yPosition,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textSize = 12.dp.toPx()
                                }
                            )
                        }
                        // Draw tick mark for each label
                        drawLine(
                            color = Color.Black,
                            start = Offset(0f, yPosition),
                            end = Offset(8.dp.toPx(), yPosition),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Build the path for the chart line based on data points
                    data.forEachIndexed { index, point ->
                        val x = index * widthPerDataPoint
                        val y =
                            size.height - ((point.closePrice - minPrice) / heightRange * size.height).toFloat()
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    // Draw the path with specific style attributes
                    drawPath(
                        path = path,
                        color = chartColor,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Add the "historical prices" label in the bottom right corner
                Text(
                    text = "historical prices",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHistoricalPriceChart() {
    val sampleData = listOf(
        HistoricalPriceData(timestamp = "2024-10-29T12:50:00+00:00", closePrice = 100.0),
        HistoricalPriceData(timestamp = "2024-10-29T12:51:00+00:00", closePrice = 120.0),
        HistoricalPriceData(timestamp = "2024-10-29T12:52:00+00:00", closePrice = 130.0),
        HistoricalPriceData(timestamp = "2024-10-29T12:53:00+00:00", closePrice = 50.0),
        HistoricalPriceData(timestamp = "2024-10-29T12:54:00+00:00", closePrice = 107.0)
    )
    MagniseTheme {
        HistoricalPriceChart(data = sampleData)
    }
}
