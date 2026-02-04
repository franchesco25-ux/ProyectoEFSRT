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
    private lateinit var etCategoria: EditText
    private lateinit var btnActualizar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_gasto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarEditarGasto)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        etDescripcion = findViewById(R.id.etDescripcionGasto)
        etMonto = findViewById(R.id.etMontoGasto)
        etCategoria = findViewById(R.id.etCategoriaGasto)
        btnActualizar = findViewById(R.id.btnActualizarGasto)
        btnCancelar = findViewById(R.id.btnCancelarGasto)

        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        balanceViewModel = ViewModelProvider(this)[BalanceViewModel::class.java]

        gastoId = intent.getIntExtra("GASTO_ID", -1)

        if (gastoId == -1) {
            Toast.makeText(this, "Error: No se encontró el gasto", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosGasto()

        btnActualizar.setOnClickListener {
            actualizarGasto()
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosGasto() {
        lifecycleScope.launch {
            val gasto = gastosViewModel.getGastoById(gastoId)

            if (gasto != null) {
                gastoActual = gasto
                etDescripcion.setText(gasto.descripcion)
                etMonto.setText(gasto.monto.toString())
                etCategoria.setText(gasto.categoria)
            } else {
                Toast.makeText(this@EditarGastoActivity, "No se pudo cargar el gasto", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun actualizarGasto() {
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
                    Toast.makeText(
                        this@EditarGastoActivity,
                        "No tienes suficiente balance para este monto (Balance actual: S/ %.2f)".format(currentBalance),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
