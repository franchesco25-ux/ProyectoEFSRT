package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.cardview.widget.CardView

class HistorialActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val cardGastos = findViewById<CardView>(R.id.cardGastos)
        val cardIngresos = findViewById<CardView>(R.id.cardIngresos)

        btnBack.setOnClickListener { finish() }

        cardGastos.setOnClickListener {
            // Usa el nuevo HistorialGastosActivity
            startActivity(Intent(this, HistorialGastosActivity::class.java))
        }

        cardIngresos.setOnClickListener {
            // Usa el HistorialIngresosActivity existente
            startActivity(Intent(this, HistorialIngresosActivity::class.java))
        }

        // Si hubiera bottom navigation
        setupCustomBottomNav("")
    }
}
