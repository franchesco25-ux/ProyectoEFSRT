package com.example.ingresosgastosapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ingresosgastosapp.Data.GastoResumen
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReporteContableActivity : AppCompatActivity() {
    private lateinit var tvIngresos: TextView
    private lateinit var tvGastosTotal: TextView
    private lateinit var tvResultado: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvInterpretacion: TextView
    private lateinit var containerGastos: LinearLayout

    private var ultimoTextoReporte: String = ""
    private var mesSeleccionadoTexto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_contable)

        // Inicializar vistas
        tvIngresos = findViewById(R.id.tvIngresosTotales)
        tvGastosTotal = findViewById(R.id.tvTotalGastos)
        tvResultado = findViewById(R.id.tvResultadoPeriodo)
        tvEstado = findViewById(R.id.tvEstadoEmoji)
        tvInterpretacion = findViewById(R.id.tvInterpretacion)
        containerGastos = findViewById(R.id.containerGastos)

        findViewById<ImageButton>(R.id.btnBackReporte).setOnClickListener { finish() }

        setupSpinner()

        val btnCompartir = findViewById<Button>(R.id.btnCompartir)

        btnCompartir.setOnClickListener {
            if (ultimoTextoReporte.isBlank()) {
                Toast.makeText(
                    this,
                    "Primero selecciona un mes para generar el reporte",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                compartirWhatsapp(ultimoTextoReporte)
            }
        }
    }

    private fun setupSpinner() {
        val meses = arrayOf(
            "Seleccionar mes",
            "Enero",
            "Febrero",
            "Marzo",
            "Abril",
            "Mayo",
            "Junio",
            "Julio",
            "Agosto",
            "Septiembre",
            "Octubre",
            "Noviembre",
            "Diciembre"
        )
        val adapter = ArrayAdapter(this, R.layout.spinner_item_white, meses)
        val spinner = findViewById<Spinner>(R.id.spinnerMes)

        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 > 0) cargarDatos(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun cargarDatos(mesInt: Int) {
        mesSeleccionadoTexto = findViewById<Spinner>(R.id.spinnerMes).selectedItem.toString()
        val mesStr = if (mesInt < 10) "0$mesInt" else "$mesInt"
        val esAcumulado = findViewById<Switch>(R.id.switchAcumulado).isChecked
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch(Dispatchers.IO) {
            val totalIngresos = if (esAcumulado) db.ingresosDao()
                .getIngresosAcumulados(mesStr, "2026") else db.ingresosDao()
                .getTotalIngresosMensual(mesStr, "2026")
            val listaGastos = db.gastosDao().getGastosResumenMensual(mesStr, "2026")

            withContext(Dispatchers.Main) {
                actualizarUI(totalIngresos ?: 0.0, listaGastos)
            }
        }
    }

    private fun actualizarUI(ingresos: Double, gastos: List<GastoResumen>) {
        val totalG = gastos.sumOf { it.total }
        val balance = ingresos - totalG

        tvIngresos.text = "Ingresos: S/ %.2f".format(ingresos)
        tvGastosTotal.text = "Total Gastos: S/ %.2f".format(totalG)
        tvResultado.text = "Resultado: S/ %.2f".format(balance)

        containerGastos.removeAllViews()

        //validación meses sin información
        if (ingresos == 0.0 && totalG == 0.0) {
            tvEstado.text = "Estado: Sin movimientos"
            tvEstado.setTextColor(Color.GRAY)
            tvInterpretacion.text =
                "No se encontraron registros para este periodo. ¡Empieza a anotar tus finanzas!"

            val tvNoData = TextView(this).apply {
                text = "No hay actividad registrada."
                setTextColor(Color.LTGRAY)
                gravity = Gravity.CENTER
                setPadding(0, 20, 0, 20)
            }
            containerGastos.addView(tvNoData)

        } else {

            if (balance >= 0) {
                tvEstado.text = "Estado: ✅ SUPERÁVIT"
                tvEstado.setTextColor(Color.GREEN)
                tvInterpretacion.text =
                    "✔ Tus ingresos cubren tus gastos. Mantén este ritmo de ahorro."
            } else {
                tvEstado.text = "Estado: ⚠ DÉFICIT"
                tvEstado.setTextColor(Color.RED)
                tvInterpretacion.text =
                    "⚠ Tus gastos superan tus ingresos. Recomendamos reducir gastos en las categorías más altas."
            }

            //ordenar la lista
            gastos.forEach { g ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 6
                        bottomMargin = 6
                    }
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(8, 8, 8, 8)
                }

                val tvCat = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    text = "• ${g.categoria}"
                    setTextColor(Color.WHITE)
                    textSize = 15f
                    maxLines = 1
                    ellipsize = android.text.TextUtils.TruncateAt.END
                }

                val tvMonto = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = "S/ %.2f".format(g.total)
                    setTextColor(Color.WHITE)
                    textSize = 15f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                }

                row.addView(tvCat)
                row.addView(tvMonto)
                containerGastos.addView(row)
            }
        }
        val detalleGastosTexto = if (gastos.isEmpty()) {
            "- Sin gastos registrados"
        } else {
            gastos.joinToString(separator = "\n") { g ->
                "• ${g.categoria}: S/ %.2f".format(g.total)
            }
        }

        val estadoTexto = tvEstado.text.toString()

        ultimoTextoReporte = """
📄 REPORTE CONTABLE - ${mesSeleccionadoTexto.uppercase()}

Ingresos: ${tvIngresos.text.toString().replace("Ingresos: ", "")}
Total Gastos: ${tvGastosTotal.text.toString().replace("Total Gastos: ", "")}
Resultado: ${tvResultado.text.toString().replace("Resultado: ", "")}
$estadoTexto

Detalle de gastos:
$detalleGastosTexto

${tvInterpretacion.text}
""".trimIndent()
    }


    private fun compartirWhatsapp(texto: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, texto)
                setPackage("com.whatsapp") // abre WhatsApp directo
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Si no tiene WhatsApp, abre el menú para compartir con otras apps
            val chooser = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, texto)
                },
                "Compartir reporte"
            )
            startActivity(chooser)
        }
    }
}
