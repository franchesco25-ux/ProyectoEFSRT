package com.example.ingresosgastosapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.viewModels
import com.example.ingresosgastosapp.Data.*
import com.example.ingresosgastosapp.views.BarChartView
import com.example.ingresosgastosapp.views.PieChartView
import java.text.SimpleDateFormat
import java.util.*

class AnalisisActivity : BaseActivity() {

    private val gastosViewModel: GastosViewModel by viewModels()
    private val ingresosViewModel: IngresosViewModel by viewModels()
    private val balanceViewModel: BalanceViewModel by viewModels()

    private lateinit var pieChart: PieChartView
    private lateinit var barChart: BarChartView
    private lateinit var layoutPieLegend: LinearLayout
    private lateinit var tvAlertaMsg: TextView

    private var todosLosGastos = listOf<Gastos>()
    private var todosLosIngresos = listOf<Ingresos>()
    private var currencySymbol = "$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analisis)

        // Views
        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)
        layoutPieLegend = findViewById(R.id.layoutPieLegend)
        tvAlertaMsg = findViewById(R.id.tvAlertaMsg)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnVerDetalles = findViewById<TextView>(R.id.btnVerDetalles)

        btnBack.setOnClickListener { finish() }

        // Currency
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
        currencySymbol = if (monedaPref != null && monedaPref.contains("("))
            monedaPref.substringAfter("(").replace(")", "") else "$"

        // Observe data
        gastosViewModel.readAllData.observe(this) { gastos ->
            todosLosGastos = gastos
            actualizarPieChart()
            actualizarBarChart()
            actualizarAlerta()
        }

        ingresosViewModel.readAllData.observe(this) { ingresos ->
            todosLosIngresos = ingresos
            actualizarBarChart()
            actualizarAlerta()
        }

        btnVerDetalles.setOnClickListener {
            startActivity(Intent(this, AhorrosActivity::class.java))
        }
    }

    private fun actualizarPieChart() {
        if (todosLosGastos.isEmpty()) {
            pieChart.setData(emptyList(), "${currencySymbol}0")
            layoutPieLegend.removeAllViews()
            return
        }

        // Filtrar gastos del mes actual
        val calendar = Calendar.getInstance()
        val mesActual = calendar.get(Calendar.MONTH)
        val anioActual = calendar.get(Calendar.YEAR)

        val gastosMes = todosLosGastos.filter { gasto ->
            val fecha = parseFecha(gasto.fecha)
            if (fecha != null) {
                val cal = Calendar.getInstance().apply { time = fecha }
                cal.get(Calendar.MONTH) == mesActual && cal.get(Calendar.YEAR) == anioActual
            } else false
        }

        // Agrupar por categoría
        val porCategoria = gastosMes.groupBy { it.categoria.ifBlank { "Otros" } }
            .mapValues { it.value.sumOf { g -> g.monto } }
            .toList()
            .sortedByDescending { it.second }

        val totalGastos = porCategoria.sumOf { it.second }

        val slices = porCategoria.mapIndexed { index, (cat, monto) ->
            PieChartView.Slice(
                label = cat,
                value = monto.toFloat(),
                color = PieChartView.CHART_COLORS[index % PieChartView.CHART_COLORS.size]
            )
        }

        pieChart.setData(slices, "$currencySymbol${String.format("%.0f", totalGastos)}")

        // Build legend dynamically
        layoutPieLegend.removeAllViews()

        // Create rows of 2 items
        var rowLayout: LinearLayout? = null
        for ((index, slice) in slices.withIndex()) {
            if (index % 2 == 0) {
                rowLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = 8 }
                }
                layoutPieLegend.addView(rowLayout)
            }

            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val dot = android.view.View(this).apply {
                layoutParams = LinearLayout.LayoutParams(24, 24).apply { marginEnd = 16 }
                setBackgroundColor(slice.color)
            }
            // Make dot circular
            val dotDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(slice.color)
            }
            dot.background = dotDrawable

            val pct = if (totalGastos > 0) (slice.value / totalGastos.toFloat() * 100).toInt() else 0
            val label = TextView(this).apply {
                text = "${slice.label} ($pct%)"
                setTextColor(Color.parseColor("#B3FFFFFF"))
                textSize = 13f
            }

            itemLayout.addView(dot)
            itemLayout.addView(label)
            rowLayout?.addView(itemLayout)
        }
    }

    private fun actualizarBarChart() {
        val calendar = Calendar.getInstance()
        val mesActual = calendar.get(Calendar.MONTH)
        val anioActual = calendar.get(Calendar.YEAR)

        val mesesNombres = arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")

        val groups = mutableListOf<BarChartView.BarGroup>()

        for (i in 5 downTo 0) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.MONTH, mesActual - i)
                set(Calendar.YEAR, anioActual)
                // Handle year rollover
                if (get(Calendar.MONTH) < 0) {
                    set(Calendar.MONTH, get(Calendar.MONTH) + 12)
                    set(Calendar.YEAR, get(Calendar.YEAR) - 1)
                }
            }
            val mes = cal.get(Calendar.MONTH)
            val anio = cal.get(Calendar.YEAR)

            val ingMes = todosLosIngresos.filter { ing ->
                val fecha = parseFecha(ing.fecha)
                if (fecha != null) {
                    val c = Calendar.getInstance().apply { time = fecha }
                    c.get(Calendar.MONTH) == mes && c.get(Calendar.YEAR) == anio
                } else false
            }.sumOf { it.monto }

            val gasMes = todosLosGastos.filter { gas ->
                val fecha = parseFecha(gas.fecha)
                if (fecha != null) {
                    val c = Calendar.getInstance().apply { time = fecha }
                    c.get(Calendar.MONTH) == mes && c.get(Calendar.YEAR) == anio
                } else false
            }.sumOf { it.monto }

            groups.add(BarChartView.BarGroup(mesesNombres[mes], ingMes.toFloat(), gasMes.toFloat()))
        }

        barChart.setData(groups)
    }

    private fun actualizarAlerta() {
        val calendar = Calendar.getInstance()
        val mesActual = calendar.get(Calendar.MONTH)
        val anioActual = calendar.get(Calendar.YEAR)

        val totalIngresosMes = todosLosIngresos.filter { ing ->
            val fecha = parseFecha(ing.fecha)
            if (fecha != null) {
                val c = Calendar.getInstance().apply { time = fecha }
                c.get(Calendar.MONTH) == mesActual && c.get(Calendar.YEAR) == anioActual
            } else false
        }.sumOf { it.monto }

        val totalGastosMes = todosLosGastos.filter { gas ->
            val fecha = parseFecha(gas.fecha)
            if (fecha != null) {
                val c = Calendar.getInstance().apply { time = fecha }
                c.get(Calendar.MONTH) == mesActual && c.get(Calendar.YEAR) == anioActual
            } else false
        }.sumOf { it.monto }

        if (totalIngresosMes > 0) {
            val pct = ((totalGastosMes / totalIngresosMes) * 100).toInt()
            tvAlertaMsg.text = when {
                pct >= 100 -> "⚠ ¡Has superado tu presupuesto! Gastos: ${currencySymbol}${String.format("%.2f", totalGastosMes)}"
                pct >= 80 -> "¡Atención! Has usado el $pct% de tus ingresos este mes."
                pct >= 50 -> "Vas bien. Has usado el $pct% de tus ingresos este mes."
                else -> "¡Excelente! Solo has usado el $pct% de tus ingresos."
            }
        } else {
            tvAlertaMsg.text = "No hay ingresos registrados este mes."
        }
    }

    private fun parseFecha(fecha: String): Date? {
        return try {
            val parte = fecha.split("T")[0]
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(parte)
        } catch (e: Exception) {
            try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha)
            } catch (e2: Exception) {
                null
            }
        }
    }
}
