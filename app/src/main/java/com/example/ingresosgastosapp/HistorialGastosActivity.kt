package com.example.ingresosgastosapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ingresosgastosapp.Adapter.HistorialAdapter
import com.example.ingresosgastosapp.Data.*
import com.example.ingresosgastosapp.databinding.ActivityHistorialGastosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HistorialGastosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialGastosBinding
    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var ingresosViewModel: IngresosViewModel
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var adapter: HistorialAdapter

    // Listas completas de datos
    private var todosLosIngresos = listOf<Ingresos>()
    private var todosLosGastos = listOf<Gastos>()

    // Filtros
    private var fechaInicio: String? = null
    private var fechaFin: String? = null
    private var tipoSeleccionado = "Todas las transacciones"
    private var categoriaSeleccionada = "Todas las categorías"

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialGastosBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbarHistorial)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbarHistorial.setNavigationOnClickListener {
            finish()
        }

        setupRecyclerView()
        setupViewModels()
        setupSpinners()
        setupDatePickers()
        setupLimpiarFiltros()
    }

    private fun setupRecyclerView() {
        adapter = HistorialAdapter(
            onEditClick = { transaccion ->
                if (transaccion.tipo == TipoTransaccion.INGRESO) {
                    val intent = android.content.Intent(this, EditarIngresoActivity::class.java)
                    intent.putExtra("INGRESO_ID", transaccion.id)
                    startActivity(intent)
                } else {
                    val intent = android.content.Intent(this, EditarGastoActivity::class.java)
                    intent.putExtra("GASTO_ID", transaccion.id)
                    startActivity(intent)
                }
            },
            onDeleteClick = { transaccion ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar ${if (transaccion.tipo == TipoTransaccion.INGRESO) "Ingreso" else "Gasto"}")
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
                        Toast.makeText(this, "Transacción eliminada", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        binding.recyclerViewHistorial.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHistorial.adapter = adapter
    }

    private fun setupViewModels() {
        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        ingresosViewModel = ViewModelProvider(this)[IngresosViewModel::class.java]
        balanceViewModel = ViewModelProvider(this)[BalanceViewModel::class.java]

        gastosViewModel.readAllData.observe(this) { gastos ->
            todosLosGastos = gastos
            aplicarFiltros()
        }

        ingresosViewModel.readAllData.observe(this) { ingresos ->
            todosLosIngresos = ingresos
            aplicarFiltros()
        }
    }

    private fun setupSpinners() {
        val tipos = arrayOf("Todas las transacciones", "Ingresos", "Gastos")
        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapterTipo

        binding.spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tipoSeleccionado = tipos[position]
                actualizarCategoriasSegunTipo()
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        actualizarCategoriasSegunTipo()
    }

    private fun actualizarCategoriasSegunTipo() {
        val categoriasUnicas = mutableSetOf<String>()

        when (tipoSeleccionado) {
            "Ingresos" -> {
                todosLosIngresos.forEach { ingreso ->
                    if (ingreso.categoria.isNotBlank()) {
                        categoriasUnicas.add(ingreso.categoria)
                    }
                }
            }
            "Gastos" -> {
                todosLosGastos.forEach { gasto ->
                    if (gasto.categoria.isNotBlank()) {
                        categoriasUnicas.add(gasto.categoria)
                    }
                }
            }
            else -> {
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
            }
        }

        val categorias = mutableListOf("Todas las categorías")
        categorias.addAll(categoriasUnicas.sorted())

        val adapterCategoria = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = adapterCategoria

        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                categoriaSeleccionada = categorias[position]
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        binding.btnFechaInicio.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    fechaInicio = dateFormat.format(calendar.time)
                    binding.btnFechaInicio.text = fechaInicio
                    aplicarFiltros()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnFechaFin.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    fechaFin = dateFormat.format(calendar.time)
                    binding.btnFechaFin.text = fechaFin
                    aplicarFiltros()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupLimpiarFiltros() {
        binding.btnLimpiarFiltros.setOnClickListener {
            fechaInicio = null
            fechaFin = null
            binding.btnFechaInicio.text = "Fecha Inicio"
            binding.btnFechaFin.text = "Fecha Fin"
            binding.spinnerTipo.setSelection(0)
            binding.spinnerCategoria.setSelection(0)
            aplicarFiltros()
        }
    }

    private fun aplicarFiltros() {
        var transacciones = mutableListOf<TransaccionItem>()

        if (tipoSeleccionado == "Todas las transacciones" || tipoSeleccionado == "Ingresos") {
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
        }

        if (tipoSeleccionado == "Todas las transacciones" || tipoSeleccionado == "Gastos") {
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
        }

        if (categoriaSeleccionada != "Todas las categorías") {
            transacciones = transacciones.filter {
                it.categoria.equals(categoriaSeleccionada, ignoreCase = true)
            }.toMutableList()
        }

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

        if (transacciones.isEmpty()) {
            binding.recyclerViewHistorial.visibility = View.GONE
            binding.tvSinTransacciones.visibility = View.VISIBLE
        } else {
            binding.recyclerViewHistorial.visibility = View.VISIBLE
            binding.tvSinTransacciones.visibility = View.GONE
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

        binding.tvTotalIngresos.text = "S/. ${String.format("%.2f", totalIngresos)}"
        binding.tvTotalGastos.text = "S/. ${String.format("%.2f", totalGastos)}"

        lifecycleScope.launch {
            val balanceActual = balanceViewModel.getCurrentBalance()

            withContext(Dispatchers.Main) {
                binding.tvBalance.text = "S/. ${String.format("%.2f", balanceActual)}"

                binding.tvBalance.setTextColor(
                    if (balanceActual >= 0) android.graphics.Color.parseColor("#28A745")
                    else android.graphics.Color.parseColor("#DC3545")
                )
            }
        }
    }
}