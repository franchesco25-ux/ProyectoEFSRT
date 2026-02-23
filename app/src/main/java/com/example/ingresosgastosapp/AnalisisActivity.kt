package com.example.ingresosgastosapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import com.example.ingresosgastosapp.Data.*
import com.example.ingresosgastosapp.DataBase.AppDatabase
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
    private lateinit var llMetasAnalisis: LinearLayout
    private lateinit var tvSinMetasAnalisis: TextView

    private var todosLosGastos = listOf<Gastos>()
    private var todosLosIngresos = listOf<Ingresos>()
    private var currencySymbol = "$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analisis)

        // 1. Inicializar Barra de Navegación (CRÍTICO)
        setupCustomBottomNav("presupuesto")

        // 2. Inicializar Vistas
        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)
        layoutPieLegend = findViewById(R.id.layoutPieLegend)
        tvAlertaMsg = findViewById(R.id.tvAlertaMsg)
        llMetasAnalisis = findViewById(R.id.ll_metas_analisis)
        tvSinMetasAnalisis = findViewById(R.id.tvSinMetasAnalisis)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnVerDetalles = findViewById<TextView>(R.id.btnVerDetalles)

        // Botón atrás: volver al menú principal de forma segura
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }

        // Configuración de Moneda
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
        currencySymbol = if (monedaPref != null && monedaPref.contains("("))
            monedaPref.substringAfter("(").replace(")", "") else "$"

        // Observar datos de Room
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

        cargarMetasAhorro()
    }

    private fun cargarMetasAhorro() {
        val dao = AppDatabase.getDatabase(this).metaAhorroDao()
        dao.getAllMetas().observe(this) { metas ->
            llMetasAnalisis.removeAllViews()
            if (metas.isNullOrEmpty()) {
                tvSinMetasAnalisis.visibility = View.VISIBLE
                return@observe
            }
            tvSinMetasAnalisis.visibility = View.GONE
            for (meta in metas) {
                llMetasAnalisis.addView(crearMetaCard(meta))
            }
        }
    }

    private fun crearMetaCard(meta: MetaAhorro): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(16))
            background = getDrawable(R.drawable.bg_stitch_card)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
        }

        val iconFrame = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
            background = getDrawable(R.drawable.bg_stitch_card)
            try {
                backgroundTintList = ColorStateList.valueOf(Color.argb(26, Color.red(Color.parseColor(meta.color)), Color.green(Color.parseColor(meta.color)), Color.blue(Color.parseColor(meta.color))))
            } catch (_: Exception) {
                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A0DF259"))
            }
        }

        val iconView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER)
            val resId = resources.getIdentifier(meta.icono, "drawable", packageName)
            setImageResource(if (resId != 0) resId else R.drawable.ic_goal)
            try {
                imageTintList = ColorStateList.valueOf(Color.parseColor(meta.color))
            } catch (_: Exception) {
                imageTintList = ColorStateList.valueOf(Color.parseColor("#0DF259"))
            }
        }
        iconFrame.addView(iconView)
        card.addView(iconFrame)

        val textSection = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = dp(14) }
        }

        val tvName = TextView(this).apply {
            text = meta.nombre
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        val tvMeta = TextView(this).apply {
            text = "Meta: $currencySymbol${String.format("%.2f", meta.montoObjetivo)}"
            setTextColor(Color.parseColor("#66FFFFFF"))
            textSize = 12f
        }

        textSection.addView(tvName)
        textSection.addView(tvMeta)
        card.addView(textSection)

        val rightSection = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val tvTotal = TextView(this).apply {
            text = "$currencySymbol${String.format("%.2f", meta.montoActual)}"
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        val pct = if (meta.montoObjetivo > 0) ((meta.montoActual / meta.montoObjetivo) * 100).toInt().coerceAtMost(100) else 0
        val tvPct = TextView(this).apply {
            text = "$pct% completado"
            try { setTextColor(Color.parseColor(meta.color)) } catch (_: Exception) { setTextColor(Color.parseColor("#0DF259")) }
            textSize = 10f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        rightSection.addView(tvTotal)
        rightSection.addView(tvPct)
        card.addView(rightSection)

        return card
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun actualizarPieChart() {
        if (todosLosGastos.isEmpty()) {
            pieChart.setData(emptyList(), "${currencySymbol}0")
            layoutPieLegend.removeAllViews()
            return
        }
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

        val porCategoria = gastosMes.groupBy { it.categoria.ifBlank { "Otros" } }
            .mapValues { it.value.sumOf { g -> g.monto } }
            .toList().sortedByDescending { it.second }

        val totalGastos = porCategoria.sumOf { it.second }
        val slices = porCategoria.mapIndexed { index, (cat, monto) ->
            PieChartView.Slice(cat, monto.toFloat(), PieChartView.CHART_COLORS[index % PieChartView.CHART_COLORS.size])
        }

        pieChart.setData(slices, "$currencySymbol${String.format("%.0f", totalGastos)}")
        layoutPieLegend.removeAllViews()

        var rowLayout: LinearLayout? = null
        for ((index, slice) in slices.withIndex()) {
            if (index % 2 == 0) {
                rowLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = 8 }
                }
                layoutPieLegend.addView(rowLayout)
            }
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(24, 24).apply { marginEnd = 16 }
                background = android.graphics.drawable.GradientDrawable().apply { shape = android.graphics.drawable.GradientDrawable.OVAL; setColor(slice.color) }
            }
            val label = TextView(this).apply {
                val pct = if (totalGastos > 0) (slice.value / totalGastos.toFloat() * 100).toInt() else 0
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
        val mesesNombres = arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
        val groups = mutableListOf<BarChartView.BarGroup>()

        for (i in 5 downTo 0) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.MONTH, mesActual - i)
                set(Calendar.YEAR, anioActual)
                if (get(Calendar.MONTH) < 0) { set(Calendar.MONTH, get(Calendar.MONTH) + 12); set(Calendar.YEAR, get(Calendar.YEAR) - 1) }
            }
            val mes = cal.get(Calendar.MONTH); val anio = cal.get(Calendar.YEAR)
            val ingMes = todosLosIngresos.filter { it -> val f = parseFecha(it.fecha); if (f != null) { val c = Calendar.getInstance().apply { time = f }; c.get(Calendar.MONTH) == mes && c.get(Calendar.YEAR) == anio } else false }.sumOf { it.monto }
            val gasMes = todosLosGastos.filter { it -> val f = parseFecha(it.fecha); if (f != null) { val c = Calendar.getInstance().apply { time = f }; c.get(Calendar.MONTH) == mes && c.get(Calendar.YEAR) == anio } else false }.sumOf { it.monto }
            groups.add(BarChartView.BarGroup(mesesNombres[mes], ingMes.toFloat(), gasMes.toFloat()))
        }
        barChart.setData(groups)
    }

    private fun actualizarAlerta() {
        val calendar = Calendar.getInstance()
        val mesActual = calendar.get(Calendar.MONTH)
        val anioActual = calendar.get(Calendar.YEAR)
        val totalIngresosMes = todosLosIngresos.filter { it -> val f = parseFecha(it.fecha); if (f != null) { val c = Calendar.getInstance().apply { time = f }; c.get(Calendar.MONTH) == mesActual && c.get(Calendar.YEAR) == anioActual } else false }.sumOf { it.monto }
        val totalGastosMes = todosLosGastos.filter { it -> val f = parseFecha(it.fecha); if (f != null) { val c = Calendar.getInstance().apply { time = f }; c.get(Calendar.MONTH) == mesActual && c.get(Calendar.YEAR) == anioActual } else false }.sumOf { it.monto }

        if (totalIngresosMes > 0) {
            val pct = ((totalGastosMes / totalIngresosMes) * 100).toInt()
            tvAlertaMsg.text = when {
                pct >= 100 -> "⚠ ¡Presupuesto superado! Gastos: $currencySymbol%.2f".format(totalGastosMes)
                pct >= 80 -> "¡Atención! Has usado el $pct% de tus ingresos."
                else -> "Balance positivo. Has usado el $pct% de tus ingresos."
            }
        } else {
            tvAlertaMsg.text = "Registra ingresos para ver tu análisis."
        }
    }

    private fun parseFecha(fecha: String): Date? {
        return try {
            val parte = fecha.split("T")[0]
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(parte)
        } catch (e: Exception) {
            try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fecha) } catch (e2: Exception) { null }
        }
    }
}
