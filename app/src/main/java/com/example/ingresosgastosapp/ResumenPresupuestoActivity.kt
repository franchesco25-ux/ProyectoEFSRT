package com.example.ingresosgastosapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class ResumenPresupuestoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen_presupuesto)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Bottom Nav personalizado
        setupCustomBottomNav("presupuesto")

        findViewById<android.view.View>(R.id.btn_revisar_planes).setOnClickListener {
            Toast.makeText(this, "Revisar planes próximamente", Toast.LENGTH_SHORT).show()
        }
    }
}