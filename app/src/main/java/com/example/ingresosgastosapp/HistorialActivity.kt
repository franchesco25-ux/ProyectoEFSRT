package com.example.ingresosgastosapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Adapter.HistorialAdapter
import com.example.ingresosgastosapp.Data.*
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class HistorialActivity : BaseActivity() {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var tvTotalIngresos: TextView
    private lateinit var tvTotalGastos: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvSinTransacciones: TextView
    private lateinit var actvCategoria: AutoCompleteTextView
    private lateinit var btnFechaInicio: MaterialButton
    private lateinit var btnFechaFin: MaterialButton
    private lateinit var btnLimpiarFiltros: MaterialButton
    private lateinit var adapter: HistorialAdapter

    private val ingresosViewModel: IngresosViewModel by viewModels()
    private val gastosViewModel: GastosViewModel by viewModels()
    private val balanceViewModel: BalanceViewModel by viewModels()

    // Listas completas de datos
    private var todosLosIngresos = listOf<Ingresos>()
    private var todosLosGastos = listOf<Gastos>()

    // Filtros
    private var fechaInicio: String? = null
    private var fechaFin: String? = null
    private var categoriaSeleccionada = "Todas las categorías"

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        // 1. Inicialización de Vistas
        rvHistorial = findViewById(R.id.rvHistorial)
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos)
        tvTotalGastos = findViewById(R.id.tvTotalGastos)
        tvBalance = findViewById(R.id.tvBalance)
        tvSinTransacciones = findViewById(R.id.tvSinTransacciones)
        actvCategoria = findViewById(R.id.actvCategoria)
        btnFechaInicio = findViewById(R.id.btnFechaInicio)
        btnFechaFin = findViewById(R.id.btnFechaFin)
        btnLimpiarFiltros = findViewById(R.id.btnLimpiarFiltros)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // 2. Configurar RecyclerView con adapter
        setupRecyclerView()

        // 3. Observar datos reales
        setupViewModels()

        // 4. Configurar filtros
        setupCategoriaCombo()
        setupDatePickers()
        setupLimpiarFiltros()
    }

    private fun setupRecyclerView() {
        adapter = HistorialAdapter(
            onEditClick = { transaccion ->
                if (transaccion.tipo == TipoTransaccion.INGRESO) {
                    val intent = Intent(this, EditarIngresoActivity::class.java)
                    intent.putExtra("INGRESO_ID", transaccion.id)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, EditarGastoActivity::class.java)
                    intent.putExtra("GASTO_ID", transaccion.id)
                    startActivity(intent)
                }
            },
            onDeleteClick = { transaccion ->
                val tipoNombre = if (transaccion.tipo == TipoTransaccion.INGRESO) "Ingreso" else "Gasto"
                AlertDialog.Builder(this)
                    .setTitle("Eliminar $tipoNombre")
                    .setMessage("¿Estás seguro de eliminar '${transaccion.descripcion}'? Esto también afectará tu balance.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        if (transaccion.tipo == TipoTransaccion.INGRESO) {
                            val ingreso = Ingresos(
                                id = transaccion.id,
                                descripcion = transaccion.descripcion,
                                monto = transaccion.monto,
                                categoria = transaccion.categoria,
                                fecha = transaccion.fecha
                            )
                            ingresosViewModel.deleteIngresosWithBalance(ingreso)
                        } else {
                            val gasto = Gastos(
                                id = transaccion.id,
                                descripcion = transaccion.descripcion,
                                monto = transaccion.monto,
                                categoria = transaccion.categoria,
                                fecha = transaccion.fecha
                            )
                            gastosViewModel.deleteGastoWithBalance(gasto)
                        }
                        Toast.makeText(this, "$tipoNombre eliminado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        rvHistorial.layoutManager = LinearLayoutManager(this)
        rvHistorial.adapter = adapter
    }

    private fun setupViewModels() {
        ingresosViewModel.readAllData.observe(this) { ingresos ->
            todosLosIngresos = ingresos
            actualizarCategoriasDisponibles()
            aplicarFiltros()
        }

        gastosViewModel.readAllData.observe(this) { gastos ->
            todosLosGastos = gastos
            actualizarCategoriasDisponibles()
            aplicarFiltros()
        }
    }

    private fun setupCategoriaCombo() {
        actvCategoria.setOnClickListener {
            actvCategoria.showDropDown()
        }
        actvCategoria.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) actvCategoria.showDropDown()
        }
        actvCategoria.setOnItemClickListener { parent, _, position, _ ->
            categoriaSeleccionada = parent.getItemAtPosition(position) as String
            aplicarFiltros()
        }
    }

    private fun actualizarCategoriasDisponibles() {
        val categoriasUnicas = mutableSetOf<String>()

        todosLosIngresos.forEach { ingreso ->
            if (ingreso.categoria.isNotBlank()) {
                categoriasUnicas.add(ingreso.categoria)
            }
        }
        todosLosGastos.forEach { gasto ->
            if (gasto.categoria.isNotBlank()) {
                categoriasUnicas.add(gasto.categoria)
            }
        }

        val categorias = mutableListOf("Todas las categorías")
        categorias.addAll(categoriasUnicas.sorted())

        val adapterCat = ArrayAdapter(this, R.layout.item_dropdown_dark, categorias)
        actvCategoria.setAdapter(adapterCat)
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        btnFechaInicio.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    fechaInicio = dateFormat.format(calendar.time)
                    btnFechaInicio.text = fechaInicio
                    aplicarFiltros()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnFechaFin.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    fechaFin = dateFormat.format(calendar.time)
                    btnFechaFin.text = fechaFin
                    aplicarFiltros()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupLimpiarFiltros() {
        btnLimpiarFiltros.setOnClickListener {
            fechaInicio = null
            fechaFin = null
            categoriaSeleccionada = "Todas las categorías"
            btnFechaInicio.text = "Fecha Inicio"
            btnFechaFin.text = "Fecha Fin"
            actvCategoria.setText("", false)
            aplicarFiltros()
        }
    }

    private fun aplicarFiltros() {
        var transacciones = mutableListOf<TransaccionItem>()

        // Agregar todos los ingresos
        todosLosIngresos.forEach { ingreso ->
            transacciones.add(
                TransaccionItem(
                    id = ingreso.id,
                    descripcion = ingreso.descripcion,
                    monto = ingreso.monto,
                    categoria = ingreso.categoria,
                    fecha = ingreso.fecha,
                    tipo = TipoTransaccion.INGRESO
                )
            )
        }

        // Agregar todos los gastos
        todosLosGastos.forEach { gasto ->
            transacciones.add(
                TransaccionItem(
                    id = gasto.id,
                    descripcion = gasto.descripcion,
                    monto = gasto.monto,
                    categoria = gasto.categoria,
                    fecha = gasto.fecha,
                    tipo = TipoTransaccion.GASTO
                )
            )
        }

        // Filtrar por categoría
        if (categoriaSeleccionada != "Todas las categorías") {
            transacciones = transacciones.filter {
                it.categoria.equals(categoriaSeleccionada, ignoreCase = true)
            }.toMutableList()
        }

        // Filtrar por rango de fechas
        if (fechaInicio != null || fechaFin != null) {
            transacciones = transacciones.filter { transaccion ->
                val fechaTransaccion = try {
                    val fechaParte = transaccion.fecha.split("T")[0]
                    val formatoISO = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    formatoISO.parse(fechaParte)
                } catch (e: Exception) {
                    try {
                        dateFormat.parse(transaccion.fecha)
                    } catch (e2: Exception) {
                        null
                    }
                }

                if (fechaTransaccion == null) return@filter false

                val cumpleFechaInicio = if (fechaInicio != null) {
                    val inicio = dateFormat.parse(fechaInicio!!)
                    val calTransaccion = Calendar.getInstance().apply { time = fechaTransaccion }
                    val calInicio = Calendar.getInstance().apply { time = inicio!! }
                    calTransaccion.set(Calendar.HOUR_OF_DAY, 0)
                    calTransaccion.set(Calendar.MINUTE, 0)
                    calTransaccion.set(Calendar.SECOND, 0)
                    calTransaccion.set(Calendar.MILLISECOND, 0)
                    calInicio.set(Calendar.HOUR_OF_DAY, 0)
                    calInicio.set(Calendar.MINUTE, 0)
                    calInicio.set(Calendar.SECOND, 0)
                    calInicio.set(Calendar.MILLISECOND, 0)
                    calTransaccion.time >= calInicio.time
                } else true

                val cumpleFechaFin = if (fechaFin != null) {
                    val fin = dateFormat.parse(fechaFin!!)
                    val calTransaccion = Calendar.getInstance().apply { time = fechaTransaccion }
                    val calFin = Calendar.getInstance().apply { time = fin!! }
                    calTransaccion.set(Calendar.HOUR_OF_DAY, 0)
                    calTransaccion.set(Calendar.MINUTE, 0)
                    calTransaccion.set(Calendar.SECOND, 0)
                    calTransaccion.set(Calendar.MILLISECOND, 0)
                    calFin.set(Calendar.HOUR_OF_DAY, 23)
                    calFin.set(Calendar.MINUTE, 59)
                    calFin.set(Calendar.SECOND, 59)
                    calFin.set(Calendar.MILLISECOND, 999)
                    calTransaccion.time <= calFin.time
                } else true

                cumpleFechaInicio && cumpleFechaFin
            }.toMutableList()
        }

        // Mostrar/ocultar estado vacío
        if (transacciones.isEmpty()) {
            rvHistorial.visibility = View.GONE
            tvSinTransacciones.visibility = View.VISIBLE
        } else {
            rvHistorial.visibility = View.VISIBLE
            tvSinTransacciones.visibility = View.GONE
        }

        adapter.setData(transacciones)
        calcularTotales(transacciones)
    }

    private fun calcularTotales(transacciones: List<TransaccionItem>) {
        val totalIngresos = transacciones
            .filter { it.tipo == TipoTransaccion.INGRESO }
            .sumOf { it.monto }

        val totalGastos = transacciones
            .filter { it.tipo == TipoTransaccion.GASTO }
            .sumOf { it.monto }

        val balanceCalculado = totalIngresos - totalGastos

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
        val currencySymbol = if (monedaPref != null && monedaPref.contains("(")) monedaPref.substringAfter("(").replace(")", "") else "$"

        tvTotalIngresos.text = "$currencySymbol %.2f".format(totalIngresos)
        tvTotalGastos.text = "$currencySymbol %.2f".format(totalGastos)
        tvBalance.text = "$currencySymbol %.2f".format(balanceCalculado)

        tvBalance.setTextColor(
            if (balanceCalculado >= 0) android.graphics.Color.parseColor("#0df259")
            else android.graphics.Color.parseColor("#EF4444")
        )
    }
}
