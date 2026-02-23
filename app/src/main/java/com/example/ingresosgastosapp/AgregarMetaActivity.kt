package com.example.ingresosgastosapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import java.util.*

class AgregarMetaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_meta)

        val btnBack = findViewById<LinearLayout>(R.id.btnBackMeta)
        val etFecha = findViewById<EditText>(R.id.etFechaMeta)
        val btnCrear = findViewById<MaterialButton>(R.id.btnCrearMeta)
        val etNombre = findViewById<EditText>(R.id.etNombreMeta)
        val etMonto = findViewById<EditText>(R.id.etMontoMeta)

        // 1. Botón Volver
        btnBack.setOnClickListener { finish() }

        // 2. Selector de Fecha (DatePicker)
        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val dateStr = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                etFecha.setText(dateStr)
            }, year, month, day)
            dpd.show()
        }

        // 3. Lógica de Creación (Simulada por ahora)
        btnCrear.setOnClickListener {
            val nombre = etNombre.text.toString()
            val monto = etMonto.text.toString()
            
            if (nombre.isNotEmpty() && monto.isNotEmpty()) {
                Toast.makeText(this, "Meta '$nombre' creada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
