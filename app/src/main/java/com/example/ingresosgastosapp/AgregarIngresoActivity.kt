package com.example.ingresosgastosapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AgregarIngresoActivity : AppCompatActivity() {

    private lateinit var etMonto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ingreso)

        etMonto = findViewById(R.id.etMontoIngreso)
        etDescripcion = findViewById(R.id.etDescripcionIngreso)
        btnGuardar = findViewById(R.id.btnGuardarIngreso)

        btnGuardar.setOnClickListener {
            val monto = etMonto.text.toString().toDoubleOrNull()
            val descripcion = etDescripcion.text.toString()

            if (monto == null || descripcion.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Aquí después guardaremos el ingreso en la base de datos
                Toast.makeText(this, "Ingreso guardado: $monto - $descripcion", Toast.LENGTH_SHORT).show()
                finish() // Volver a la pantalla principal
            }
        }
    }
}
