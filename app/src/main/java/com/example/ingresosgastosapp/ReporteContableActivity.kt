package com.example.ingresosgastosapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ingresosgastosapp.Data.GastoResumen
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 1. Declaramos las variables como propiedades de la clase
private lateinit var tvIngresosTotales: TextView
private lateinit var tvTotalGastos: TextView
private lateinit var tvResultado: TextView
private lateinit var tvEstado: TextView
private lateinit var tvInterpretacion: TextView
private lateinit var containerGastos: LinearLayout
class ReporteContableActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_contable)
        findViewById<ImageButton>(R.id.btnBackReporte).setOnClickListener {
            finish() // Esto cierra la actividad actual y te regresa a Análisis
        }

        // 2. Buscamos cada vista por el ID que pusimos en el XML
        tvIngresosTotales = findViewById(R.id.tvIngresosTotales)
        tvTotalGastos = findViewById(R.id.tvTotalGastos)
        tvResultado = findViewById(R.id.tvResultadoPeriodo)
        tvEstado = findViewById(R.id.tvEstadoEmoji)
        tvInterpretacion = findViewById(R.id.tvInterpretacion)
        containerGastos = findViewById(R.id.containerGastos)

        findViewById<Switch>(R.id.switchAcumulado).setOnCheckedChangeListener { _, _ ->
            actualizarReporte()
        }

        setupSpinner()

        // Listener para cambios en el spinner o el switch
        findViewById<Spinner>(R.id.spinnerMes).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                actualizarReporte()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun actualizarReporte() {
        val spinnerMes = findViewById<Spinner>(R.id.spinnerMes)
        val mesSeleccionado = spinnerMes.selectedItem.toString()
        if (mesSeleccionado == "Seleccionar mes") return
        val esAcumulado = findViewById<Switch>(R.id.switchAcumulado).isChecked

        // Actualizar el título dinámicamente
        val tvTitulo = findViewById<TextView>(R.id.tvTituloReporte)
        tvTitulo.text = if (esAcumulado) "REPORTE ACUMULADO 2026" else "REPORTE - $mesSeleccionado 2026".uppercase()

        // Convertir nombre del mes a número para la base de datos
        val mesesMap = mapOf(
            "Enero" to "01", "Febrero" to "02", "Marzo" to "03", "Abril" to "04",
            "Mayo" to "05", "Junio" to "06", "Julio" to "07", "Agosto" to "08",
            "Septiembre" to "09", "Octubre" to "10", "Noviembre" to "11", "Diciembre" to "12"
        )
        val mesNum = mesesMap[mesSeleccionado] ?: "01"

        // Llamar a la función que realmente consulta la BD
        cargarDatosReporte(mesNum, esAcumulado)
    }

    private fun setupSpinner() {
        // Añadimos "Seleccionar mes" como primera opción [cite: 164]
        val meses = arrayOf("Seleccionar mes", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

        // Usamos un layout personalizado para que el texto sea blanco
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner = findViewById<Spinner>(R.id.spinnerMes)
        spinner.adapter = adapter

        // Esto ayuda a que la flechita del spinner se vea blanca si tu fondo es oscuro
        spinner.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
    }

    // Dentro de tu Activity
    private fun cargarDatosReporte(mesNum: String, esAcumulado: Boolean) {
        val db = AppDatabase.getDatabase(this)
        val anioActual = "2026" // Puedes obtenerlo dinámicamente con Calendar

        lifecycleScope.launch(Dispatchers.IO) {
            val ingresos: Double
            val listaGastos: List<GastoResumen>

            if (esAcumulado) {
                ingresos = db.ingresosDao().getIngresosAcumulados(anioActual) ?: 0.0
                // Para el acumulado, sumamos todos los gastos por categoría de lo que va del año
                listaGastos = db.gastosDao().getGastosResumenMensual("%", anioActual)
            } else {
                ingresos = db.ingresosDao().getTotalIngresosMensual(mesNum, anioActual) ?: 0.0
                listaGastos = db.gastosDao().getGastosResumenMensual(mesNum, anioActual)
            }

            withContext(Dispatchers.Main) {
                actualizarInterfaz(ingresos, listaGastos, esAcumulado)
            }
        }
    }

    private fun actualizarInterfaz(ingresos: Double, gastos: List<GastoResumen>, esAcumulado: Boolean) {
        val totalGastos = gastos.sumOf { it.total }
        val balance = ingresos - totalGastos

        // Llenar los TextViews
        tvIngresosTotales.text = "S/ %.2f".format(ingresos)
        tvTotalGastos.text = "S/ %.2f".format(totalGastos)
        tvResultado.text = "S/ %.2f".format(balance)

        // Mostrar lista de gastos dinámicamente
        containerGastos.removeAllViews()
        gastos.forEach { gasto ->
            val tv = TextView(this).apply {
                text = "${gasto.categoria}: S/ ${gasto.total}"
                setTextColor(Color.WHITE)
                setPadding(0, 8, 0, 8)
            }
            containerGastos.addView(tv)
        }

        // Lógica de Semáforo (Interpretación)
        if (balance >= 0) {
            tvEstado.text = "✅ SUPERÁVIT"
            tvInterpretacion.text = "¡Buen trabajo! Tus ingresos superan tus gastos. Considera invertir el excedente."
        } else {
            tvEstado.text = "⚠ DÉFICIT"
            tvInterpretacion.text = "Cuidado: Estás gastando más de lo que ganas. Revisa la categoría con mayor gasto."
        }
    }


}