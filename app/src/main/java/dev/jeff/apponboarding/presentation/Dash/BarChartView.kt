package dev.jeff.apponboarding.presentation.Dash

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun BarChartView(
    rango0: Int,
    rango26: Int,
    rango51: Int,
    rango76: Int,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            // BLOQUE FACTORY: Configuración que NO cambia (colores de fondo, ejes, leyendas)
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false // Opcional: ocultar leyenda si solo hay un set

                // Configurar Eje X para que se vea mejor
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
                // Etiquetas personalizadas en lugar de 0, 1, 2, 3
                xAxis.valueFormatter = IndexAxisValueFormatter(listOf("0-25%", "26-50%", "51-75%", "76-100%"))

                axisLeft.axisMinimum = 0f // Empezar siempre en 0
                axisRight.isEnabled = false // Ocultar eje derecho
            }
        },
        update = { chart ->
            // BLOQUE UPDATE: Se ejecuta cada vez que cambian los valores de rango
            val entries = listOf(
                BarEntry(0f, rango0.toFloat()),
                BarEntry(1f, rango26.toFloat()),
                BarEntry(2f, rango51.toFloat()),
                BarEntry(3f, rango76.toFloat()),
            )

            val set = BarDataSet(entries, "Progreso (%)").apply {
                color = Color.parseColor("#3F51B5")
                valueTextColor = Color.BLACK
                valueTextSize = 12f
            }

            val data = BarData(set)
            data.barWidth = 0.5f // Hacer las barras un poco más delgadas si quieres

            chart.data = data
            chart.invalidate() // ¡IMPORTANTE! Fuerza el redibujado del gráfico
            chart.animateY(1000) // Re-animar si cambian los datos (opcional)
        }
    )
}