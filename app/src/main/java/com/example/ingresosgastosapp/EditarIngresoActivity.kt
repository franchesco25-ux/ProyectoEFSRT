package com.example.ingresosgastosapp

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.ingresosgastosapp.BaseActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
    private lateinit var etCategoria: EditText
    private lateinit var btnActualizar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_ingreso)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarEditarIngreso)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        etDescripcion = findViewById(R.id.etDescripcion)
        etMonto = findViewById(R.id.etMonto)
        etCategoria = findViewById(R.id.etCategoria)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnCancelar = findViewById(R.id.btnCancelar)

        ingresosViewModel = ViewModelProvider(this)[IngresosViewModel::class.java]
        balanceViewModel = ViewModelProvider(this)[BalanceViewModel::class.java]

        ingresoId = intent.getIntExtra("INGRESO_ID", -1)

        if (ingresoId == -1) {
            Toast.makeText(this, "Error: No se encontró el ingreso", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosIngreso()

        btnActualizar.setOnClickListener {
            actualizarIngreso()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosIngreso() {
        lifecycleScope.launch {
            val ingreso = ingresosViewModel.getIngresoById(ingresoId)

            if (ingreso != null) {
                ingresoActual = ingreso
                etDescripcion.setText(ingreso.descripcion)
                etMonto.setText(ingreso.monto.toString())
                etCategoria.setText(ingreso.categoria)
            } else {
                Toast.makeText(this@EditarIngresoActivity, "No se pudo cargar el ingreso", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun actualizarIngreso() {
        val descripcion = etDescripcion.text.toString()
        val montoStr = etMonto.text.toString()
        val categoria = etCategoria.text.toString()

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

        // Crear el ingreso actualizado manteniendo el ID y la fecha originales
        val ingresoActualizado = Ingresos(
            id = ingresoActual!!.id,
            descripcion = descripcion,
            monto = nuevoMonto,
            categoria = categoria,
            fecha = ingresoActual!!.fecha
        )

        // Actualizar en la base de datos con ajuste de balance
        lifecycleScope.launch(Dispatchers.IO) {
            // Calcular la diferencia de monto
            val diferenciaMonto = nuevoMonto - ingresoActual!!.monto

            // Obtener balance actual
            val currentBalance = balanceViewModel.getCurrentBalance()
            val newBalance = currentBalance + diferenciaMonto

            // Actualizar ingreso y balance
            ingresosViewModel.updateIngresos(ingresoActualizado)
            balanceViewModel.updateBalance(newBalance)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditarIngresoActivity, "Ingreso actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
