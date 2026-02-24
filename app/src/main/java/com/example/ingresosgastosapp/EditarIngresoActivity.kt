package com.example.ingresosgastosapp

import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.ingresosgastosapp.CurrencyTextWatcher
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.Data.IngresosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarIngresoActivity : BaseActivity() {

    private lateinit var ingresosViewModel: IngresosViewModel
    private lateinit var balanceViewModel: BalanceViewModel
    private var ingresoActual: Ingresos? = null
    private var ingresoId: Int = -1

    private lateinit var etDescripcion: EditText
    private lateinit var etMonto: EditText
    private lateinit var spinnerCategoria: Spinner

    private val categorias = listOf(
        "Salario", "Freelance", "Venta", "Inversión",
        "Regalo", "Reembolso", "Otros"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_ingreso)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar back button
        findViewById<ImageButton>(R.id.btnBackEditarIngreso).setOnClickListener {
            finish()
        }

        etDescripcion = findViewById(R.id.etDescripcion)
        etMonto = findViewById(R.id.etMonto)
        etMonto.addTextChangedListener(CurrencyTextWatcher(etMonto))
        spinnerCategoria = findViewById(R.id.spinnerCategoriaIngreso)

        // Configurar spinner de categorías
        val adapter = ArrayAdapter(this, R.layout.item_dropdown_dark, categorias)
        adapter.setDropDownViewResource(R.layout.item_dropdown_dark)
        spinnerCategoria.adapter = adapter

        // Botones
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnActualizar)
            .setOnClickListener { actualizarIngreso() }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancelar)
            .setOnClickListener { finish() }

        ingresosViewModel = ViewModelProvider(this)[IngresosViewModel::class.java]
        balanceViewModel = ViewModelProvider(this)[BalanceViewModel::class.java]

        ingresoId = intent.getIntExtra("INGRESO_ID", -1)

        if (ingresoId == -1) {
            Toast.makeText(this, "Error: No se encontró el ingreso", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosIngreso()
    }

    private fun cargarDatosIngreso() {
        lifecycleScope.launch {
            val ingreso = ingresosViewModel.getIngresoById(ingresoId)

            if (ingreso != null) {
                ingresoActual = ingreso
                etDescripcion.setText(ingreso.descripcion)
                etMonto.setText(ingreso.monto.toString())

                // Pre-seleccionar la categoría en el spinner
                val index = categorias.indexOfFirst { it.equals(ingreso.categoria, ignoreCase = true) }
                if (index >= 0) {
                    spinnerCategoria.setSelection(index)
                }
            } else {
                Toast.makeText(this@EditarIngresoActivity, "No se pudo cargar el ingreso", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun actualizarIngreso() {
        val descripcion = etDescripcion.text.toString()
        val montoStr = etMonto.text.toString().replace(",", "")
        val categoria = spinnerCategoria.selectedItem?.toString() ?: ""

        if (TextUtils.isEmpty(descripcion) || TextUtils.isEmpty(montoStr) || TextUtils.isEmpty(categoria)) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoMonto = montoStr.toDoubleOrNull()
        if (nuevoMonto == null || nuevoMonto <= 0) {
            Toast.makeText(this, "El monto debe ser un número positivo", Toast.LENGTH_SHORT).show()
            return
        }

        if (ingresoActual == null) {
            Toast.makeText(this, "Error: No se encontró el ingreso original", Toast.LENGTH_SHORT).show()
            return
        }

        val ingresoActualizado = Ingresos(
            id = ingresoActual!!.id,
            descripcion = descripcion,
            monto = nuevoMonto,
            categoria = categoria,
            fecha = ingresoActual!!.fecha
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val diferenciaMonto = nuevoMonto - ingresoActual!!.monto

            val currentBalance = balanceViewModel.getCurrentBalance()
            val newBalance = currentBalance + diferenciaMonto

            ingresosViewModel.updateIngresos(ingresoActualizado)
            balanceViewModel.updateBalance(newBalance)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditarIngresoActivity, "Ingreso actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

