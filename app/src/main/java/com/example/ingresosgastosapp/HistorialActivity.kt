package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView

class HistorialActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val cardGastos = findViewById<CardView>(R.id.cardGastos)
        val cardIngresos = findViewById<CardView>(R.id.cardIngresos)
        val cardMetas = findViewById<CardView>(R.id.cardMetas)
        val cardExportar = findViewById<CardView>(R.id.cardExportar)

        btnBack.setOnClickListener { finish() }

        cardGastos.setOnClickListener {
            // Usa el nuevo HistorialGastosActivity
            startActivity(Intent(this, HistorialGastosActivity::class.java))
        }

        cardIngresos.setOnClickListener {
            // Usa el HistorialIngresosActivity existente
            startActivity(Intent(this, HistorialIngresosActivity::class.java))
        }

        cardMetas.setOnClickListener {
            startActivity(Intent(this, AhorrosActivity::class.java))
        }

        cardExportar.setOnClickListener {
            Toast.makeText(this, "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show()
        }

        // Si hubiera bottom navigation
        setupCustomBottomNav("")
    }
}

