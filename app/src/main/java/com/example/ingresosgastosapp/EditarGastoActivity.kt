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
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.example.ingresosgastosapp.Data.Gastos
import com.example.ingresosgastosapp.Data.GastosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarGastoActivity : BaseActivity() {

    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var balanceViewModel: BalanceViewModel
    private var gastoActual: Gastos? = null
    private var gastoId: Int = -1

    private lateinit var etDescripcion: EditText
    private lateinit var etMonto: EditText
    private lateinit var spinnerCategoria: Spinner

    private val categorias = listOf(
        "Comida", "Transporte", "Salud", "Entretenimiento",
        "Educación", "Servicios", "Ropa", "Otros"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_gasto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar back button
        findViewById<ImageButton>(R.id.btnBackEditarGasto).setOnClickListener {
            finish()
        }

        etDescripcion = findViewById(R.id.etDescripcionGasto)
        etMonto = findViewById(R.id.etMontoGasto)
        spinnerCategoria = findViewById(R.id.spinnerCategoriaGasto)

        // Configurar spinner de categorías
        val adapter = ArrayAdapter(this, R.layout.item_dropdown_dark, categorias)
        adapter.setDropDownViewResource(R.layout.item_dropdown_dark)
        spinnerCategoria.adapter = adapter

        // Botones
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnActualizarGasto)
            .setOnClickListener { actualizarGasto() }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancelarGasto)
            .setOnClickListener { finish() }

        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        balanceViewModel = ViewModelProvider(this)[BalanceViewModel::class.java]

        gastoId = intent.getIntExtra("GASTO_ID", -1)

        if (gastoId == -1) {
            Toast.makeText(this, "Error: No se encontró el gasto", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosGasto()
    }

    private fun cargarDatosGasto() {
        lifecycleScope.launch {
            val gasto = gastosViewModel.getGastoById(gastoId)

            if (gasto != null) {
                gastoActual = gasto
                etDescripcion.setText(gasto.descripcion)
                etMonto.setText(gasto.monto.toString())

                // Pre-seleccionar la categoría en el spinner
                val index = categorias.indexOfFirst { it.equals(gasto.categoria, ignoreCase = true) }
                if (index >= 0) {
                    spinnerCategoria.setSelection(index)
                }
            } else {
                Toast.makeText(this@EditarGastoActivity, "No se pudo cargar el gasto", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun actualizarGasto() {
        val descripcion = etDescripcion.text.toString()
        val montoStr = etMonto.text.toString()
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

        if (gastoActual == null) {
            Toast.makeText(this, "Error: No se encontró el gasto original", Toast.LENGTH_SHORT).show()
            return
        }

        val gastoActualizado = Gastos(
            id = gastoActual!!.id,
            descripcion = descripcion,
            monto = nuevoMonto,
            categoria = categoria,
            fecha = gastoActual!!.fecha
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val diferenciaMonto = gastoActual!!.monto - nuevoMonto

            val currentBalance = balanceViewModel.getCurrentBalance()
            val newBalance = currentBalance + diferenciaMonto

            if (newBalance >= 0) {
                gastosViewModel.updateGasto(gastoActualizado)
                balanceViewModel.updateBalance(newBalance)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarGastoActivity, "Gasto actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
                    val currencySymbol = if (monedaPref != null && monedaPref.contains("(")) monedaPref.substringAfter("(").replace(")", "") else "$"

                    Toast.makeText(
                        this@EditarGastoActivity,
                        "No tienes suficiente balance para este monto (Balance actual: $currencySymbol %.2f)".format(currentBalance),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
