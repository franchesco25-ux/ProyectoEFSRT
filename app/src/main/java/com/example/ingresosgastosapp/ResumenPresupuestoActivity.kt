package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast

class ResumenPresupuestoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presupuesto)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }

        // Bottom Nav personalizado
        setupCustomBottomNav("presupuesto")

        findViewById<android.view.View>(R.id.btn_revisar_planes).setOnClickListener {
            Toast.makeText(this, "Revisar planes próximamente", Toast.LENGTH_SHORT).show()
        }
    }
}